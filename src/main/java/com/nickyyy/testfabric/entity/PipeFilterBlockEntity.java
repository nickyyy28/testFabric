package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.PipeFilterBlock;
import com.nickyyy.testfabric.block.TransportCombinerBlock;
import com.nickyyy.testfabric.screen.PipeFilterScreenHandler;
import com.nickyyy.testfabric.util.ModLog;
import com.nickyyy.testfabric.util.Pair;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

public class PipeFilterBlockEntity extends LootableContainerBlockEntity implements BlockEntityInventory, SidedInventory, ExtendedScreenHandlerFactory {
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(15, ItemStack.EMPTY);

    public static final int MAX_COOLING = 4;

    private int cooling = -1;

    private long lastTickTime;

    public boolean findTransferDirection = false;

    public Direction from = null, to = null, otherTo = null;

    public PipeFilterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModEntities.PIPE_FILTER_ENTITY, blockPos, blockState);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return items;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        items = list;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("cooling", this.cooling);
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        this.cooling = nbt.getInt("cooling");
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(this.getPos());
        return new PipeFilterScreenHandler(syncId, playerInventory, buf);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new PipeFilterScreenHandler(i, playerInventory, this);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        checkLootInteraction(null);
        items.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
//        int[] result = new int[];
        if (side == from) {
            int[] ret = new int[9];
            for (int i = 0 ; i < ret.length ; i++) {
                ret[i] = i;
            }
            return ret;
        }
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty()) continue;
            boolean isFiltered = false;
            for (int filterItem = 9 ; filterItem < items.size() ; ++filterItem) {
                if (items.get(filterItem) == ItemStack.EMPTY) continue;
                if (items.get(filterItem).isOf(itemStack.getItem())) {
                    isFiltered = true;
                    break;
                }
            }
            if (side == to) {
                if (isFiltered) {
                    result.add(i);
                    ModLog.LOGGER.info("Filtered Item: " + itemStack.toString());
                }
            } else if (side == otherTo) {
                if (!isFiltered) {
                    result.add(i);
                }
            }

        }
        int[] arr = new int[result.size()];
        for (int i = 0 ; i < arr.length ; i++) {
            arr[i] = result.get(i);
        }
        return arr;
    }



    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir == from;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == to || dir == otherTo;
    }

    public static void server_tick(World world, BlockPos pos, BlockState state, PipeFilterBlockEntity entity) {
        entity.cooling--;
        entity.updateState(world, pos, state);

        if (CanTransfer(world, pos, state) && !entity.needsCooling()) {
            entity.setCooling(0);
            insertAndExtract(world, pos, state, entity, () -> PipeFilterBlockEntity.extract(world, entity));
        }
    }

    private static boolean insertAndExtract(World world, BlockPos pos, BlockState state, PipeFilterBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return false;
        }

        //如果冷却为0
        if (!blockEntity.needsCooling()) {
            boolean bl = false;

            //如果方块实体3x3格子不为空, 进行插入
            if (!blockEntity.isEmpty()) {
                bl = insert(world, pos, state, blockEntity);
            }
            if (!blockEntity.isFull()) {
                bl |= booleanSupplier.getAsBoolean();
            }
            if (bl) {
                blockEntity.setCooling(MAX_COOLING);
                markDirty(world, pos, state);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0 ; i < items.size() - 6 ; i++) {
            if (!items.get(i).isEmpty()) return false;
        }
        return true;
    }

    private static boolean extract(World world, PipeFilterBlockEntity entity) {
        Inventory inventory = getInputInventory(world, entity);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            if (isInventoryEmpty(inventory, direction)) {
                return false;
            }
            return getAvailableSlots(inventory, direction).anyMatch(slot -> PipeFilterBlockEntity.extract(entity, inventory, slot, direction));
        }
        return false;
    }

    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return getAvailableSlots(inv, facing).allMatch(slot -> inv.getStack(slot).isEmpty());
    }

    public boolean isFull() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount()) continue;
            return false;
        }
        return true;
    }

    private static boolean insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        //获取输出口的容器
        Inventory inventory2 = getOutputInventory(world, pos, state);
        if (inventory2 == null) {
            return false;
        }

        //获取当前方块实体的目标方向的反方向
        PipeFilterBlockEntity entity = (PipeFilterBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos));
        Direction direction = entity.to.getOpposite();
        //目标容器的可用slot是否为满,若满了则代表无法插入
        if (isInventoryFull(inventory2, direction)) {
            return false;
        }

        int[] slots = entity.getAvailableSlots(entity.to);
        ModLog.LOGGER.info("可用槽数量为:" + slots.length);
        boolean ret = false;

        for (int i = 0 ; i < slots.length ; i++) {
            if (inventory.getStack(i).isEmpty()) continue;
            ItemStack stack1 = inventory.getStack(i).copy();
            ItemStack stack2 = transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
            if (stack2.isEmpty()) {
                inventory2.markDirty();
                ret = true;
                break;
            }
            inventory.setStack(i, stack1);
        }

        Inventory inventory3 = getOtherOutputInventory(world, pos, state);
        if (inventory3 == null) {
            ModLog.LOGGER.warn("其他口为空");
            return ret;
        }

        int[] slots2 = entity.getAvailableSlots(entity.otherTo);
        for (int i = 0 ; i < slots2.length ; i++) {
            if (inventory.getStack(i).isEmpty()) continue;
            ItemStack stack1 = inventory.getStack(i).copy();
            ItemStack stack2 = transfer(inventory, inventory3, inventory.removeStack(i, 1), direction);
            if (stack2.isEmpty()) {
                inventory3.markDirty();
                ret |= true;
                break;
            }
            inventory.setStack(i, stack1);
        }

        /*for (int i = 0 ; i < inventory.size() ; i++) {
            if (inventory.getStack(i).isEmpty()) continue;
            ItemStack stack1 = inventory.getStack(i).copy();
            ItemStack stack2 = transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
            if (stack2.isEmpty()) {
                inventory2.markDirty();
                return true;
            }
            inventory.setStack(i, stack1);
        }*/

        return ret;
    }

    public static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, @Nullable Direction side) {
        if (to instanceof SidedInventory) {
            SidedInventory sidedInventory = (SidedInventory) to;
            if (side != null) {
                int[] is = sidedInventory.getAvailableSlots(side);
                int i = 0;
                while (i < is.length) {
                    if (stack.isEmpty()) return stack;
                    stack = transfer(from, to, stack, is[i], side);
                    ++i;
                }
                return stack;
            }
        }
        int j = to.size();
        int i = 0;
        while (i < j) {
            if (stack.isEmpty()) return stack;
            stack = transfer(from, to, stack, i, side);
            ++i;
        }
        return stack;
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        SidedInventory sidedInventory;
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory) || (sidedInventory = (SidedInventory) inventory).canInsert(slot, stack, side);
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.canCombine(first, second);
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);
        if (canInsert(to, stack, slot, side)) {
            int j;
            boolean bl = false;
            boolean bl2 = to.isEmpty();
            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                boolean bl3 = bl = j > 0;
            }
            if (bl) {
                PipeFilterBlockEntity entity;
                if (bl2 && to instanceof PipeFilterBlockEntity && !(entity = (PipeFilterBlockEntity) to).isDisabled()) {
                    j = 0;
                    if (from instanceof PipeFilterBlockEntity) {
                        PipeFilterBlockEntity entity1 = (PipeFilterBlockEntity) from;
                        if (entity.lastTickTime >= entity1.lastTickTime) {
                            j = 1;
                        }
                    }
                    entity.setCooling(MAX_COOLING - j);
                }
                to.markDirty();
            }
        }
        return stack;
    }

    public boolean isDisabled() {
        return this.cooling > MAX_COOLING;
    }

    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        return getAvailableSlots(inventory, direction).allMatch(slot -> {
            ItemStack itemStack = inventory.getStack(slot);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory) inventory).getAvailableSlots(side));
        }
        return IntStream.range(0, inventory.size());
    }

    private static boolean extract(PipeFilterBlockEntity entity, Inventory inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getStack(slot);
        if (!itemStack.isEmpty() && canExtract(entity, inventory, itemStack, slot, side)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = transfer(inventory, entity, inventory.removeStack(slot, 1), null);
            if (itemStack3.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setStack(slot, itemStack2);
        }
        return false;
    }

    private static boolean canExtract(PipeFilterBlockEntity entity, Inventory fromInventory, ItemStack itemStack, int slot, Direction side) {
        SidedInventory sidedInventory;
        if (!fromInventory.canTransferTo(entity, slot, itemStack)) {
            return false;
        }
        return !(fromInventory instanceof SidedInventory) || (sidedInventory = (SidedInventory) fromInventory).canExtract(slot, itemStack, side);
    }

    private boolean needsCooling() {
        return this.cooling > 0;
    }

    private void setCooling(int cooling) {
        this.cooling = cooling;
    }

    /**
     * @details 获取方块过滤口输出的容器
     * @param world
     * @param pos
     * @param state
     * @return
     */
    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        return TransportPipeBlockEntity.getInventoryByDirection(world, pos, ((PipeFilterBlockEntity) world.getBlockEntity(pos)).to);
    }

    /**
     * @details 获取方块其他口输出的容器
     * @param world
     * @param pos
     * @param state
     * @return
     */
    private static Inventory getOtherOutputInventory(World world, BlockPos pos, BlockState state) {
        return TransportPipeBlockEntity.getInventoryByDirection(world, pos, ((PipeFilterBlockEntity) world.getBlockEntity(pos)).otherTo);
    }

    private static Inventory getInputInventory(World world, BlockPos pos, BlockState state) {
        return TransportPipeBlockEntity.getInventoryByDirection(world, pos, ((PipeFilterBlockEntity) world.getBlockEntity(pos)).from);
    }

    private static Inventory getInputInventory(World world, PipeFilterBlockEntity entity) {
        BlockPos pos = entity.getPos();
        BlockEntity entity1 = world.getBlockEntity(pos.offset(entity.from));
        return (Inventory) entity1;
    }

    public static boolean CanTransfer(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof PipeFilterBlockEntity)) {
            return false;
        }

        PipeFilterBlockEntity entity = (PipeFilterBlockEntity) world.getBlockEntity(pos);
        assert entity != null;
        return entity.findTransferDirection;
    }

    private void updateState(World world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            int shape = state.get(PipeFilterBlock.FILTER_SHAPE);
            switch (shape) {
                case 0, 7 -> otherTo = Direction.WEST;
                case 1, 5 -> otherTo = Direction.DOWN;
                case 2 -> otherTo = Direction.EAST;
                case 3 -> otherTo = Direction.UP;
                case 4 -> otherTo = Direction.NORTH;
                case 6 -> otherTo = Direction.SOUTH;
            }

            Pair<Direction> pair;
            if (shape <= 3) {
                pair = new Pair<>(Direction.NORTH, Direction.SOUTH);
            } else {
                pair = new Pair<>(Direction.WEST, Direction.EAST);
            }

            BlockPos newPos = TransportPipeBlockEntity.posMove(pos, pair.var1);
            if (world.getBlockEntity(newPos) instanceof TransportPipeBlockEntity) {
//                ModLog.LOGGER.info("找到管道");
                TransportPipeBlockEntity entity = ((TransportPipeBlockEntity) world.getBlockEntity(newPos));
                if (entity.findTransferDirection && entity.to.getOpposite() == pair.var1) {
                    findTransferDirection = true;
                    from = pair.var1;
                    to = pair.var2;
                    return;
                }
//                ModLog.LOGGER.info("该管道无法传输");
            } else if (world.getBlockEntity(newPos) instanceof PipeFilterBlockEntity) {
                PipeFilterBlockEntity entity = ((PipeFilterBlockEntity) world.getBlockEntity(newPos));
                if (entity.findTransferDirection && entity.to.getOpposite() == pair.var1) {
                    findTransferDirection = true;
                    from = pair.var1;
                    to = pair.var2;
                    return;
                }
            } else if (world.getBlockEntity(newPos) instanceof TransportCombinerBlockEntity) {
//                ModLog.LOGGER.info("找到合流器");
                TransportCombinerBlockEntity entity = (TransportCombinerBlockEntity) world.getBlockEntity(newPos);
                BlockState state1 = world.getBlockState(newPos);
                if (TransportCombinerBlock.getDirectionByState(state1.get(TransportCombinerBlock.FACING)) == pair.var1) {
                    findTransferDirection = true;
                    from = pair.var1;
                    to = pair.var2;
                    return;
                }
//                ModLog.LOGGER.info("合流器方向错误");
            }
            newPos = TransportPipeBlockEntity.posMove(pos, pair.var2);
//            ModLog.LOGGER.info("搜索方块位置:" + newPos.toShortString() + " 方向: " + pair.var2.toString());
            if (world.getBlockEntity(newPos) instanceof TransportPipeBlockEntity) {
                TransportPipeBlockEntity entity = ((TransportPipeBlockEntity) world.getBlockEntity(newPos));
                if (entity.findTransferDirection && entity.to.getOpposite() == pair.var2) {
                    findTransferDirection = true;
                    from = pair.var2;
                    to = pair.var1;
                    return;
                }
//                ModLog.LOGGER.info("方向2未找到上一个管道");
            }else if (world.getBlockEntity(newPos) instanceof PipeFilterBlockEntity) {
                PipeFilterBlockEntity entity = ((PipeFilterBlockEntity) world.getBlockEntity(newPos));
                if (entity.findTransferDirection && entity.to.getOpposite() == pair.var2) {
                    findTransferDirection = true;
                    from = pair.var2;
                    to = pair.var1;
                    return;
                }
            }  else if (world.getBlockEntity(newPos) instanceof TransportCombinerBlockEntity) {
                TransportCombinerBlockEntity entity = (TransportCombinerBlockEntity) world.getBlockEntity(newPos);
                BlockState state1 = world.getBlockState(newPos);
                if (TransportCombinerBlock.getDirectionByState(state1.get(TransportCombinerBlock.FACING)) == pair.var2) {
                    findTransferDirection = true;
                    from = pair.var2;
                    to = pair.var1;
                    return;
                }
//                ModLog.LOGGER.info("方向2未找到合流器");
            }

            findTransferDirection = false;
            from = null;
            to = null;

        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
    }

    @Override
    public int size() {
        return items.size();
    }
}
