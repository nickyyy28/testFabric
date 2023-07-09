package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.TransportCombinerBlock;
import com.nickyyy.testfabric.block.TransportPipeBlock;
import com.nickyyy.testfabric.screen.TransportPipeScreenHandler;
import com.nickyyy.testfabric.util.ModLog;
import com.nickyyy.testfabric.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

public class TransportPipeEntity extends LootableContainerBlockEntity implements BlockEntityInventory, SidedInventory {
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private ItemStack itemToDisplay = ItemStack.EMPTY;

    public static TransportPipeEntity debugEntity = null;

    public Direction from = null;
    public Direction to = null;

    public static final int MAX_COOLING = 4;

    public boolean findTransferDirection = false;
    private int cooling = -1;
    private long lastTickTime;

    public TransportPipeEntity(BlockPos pos, BlockState state) {
        super(ModEntities.TRANSPORT_PIPE_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public ItemStack display() {
        return itemToDisplay;
    }

    public void setDisplay(ItemStack stack) {
        itemToDisplay = stack;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("cooling", cooling);
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
//        return new TransportPipeScreenHandler(syncId, playerInventory, );
        return null;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        this.cooling = nbt.getInt("cooling");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public static final Identifier TRANSPORT_ENTITY_PACKET_ID = new Identifier("testfabric", "transport_pipe_packet");

    private int lastServerTick = 0;
    private long lastEmptyTick = 0;
    private ItemStack lastItemToDisplay = ItemStack.EMPTY;

    public static void server_tick(World world, BlockPos pos, BlockState state, TransportPipeEntity entity) {
        entity.cooling--;
        entity.updateState(world, pos, state);
        long nowTime = world.getTime();
        ItemStack nowItemDisplay = entity.items.get(0);


        if (nowItemDisplay != ItemStack.EMPTY && entity.lastItemToDisplay != nowItemDisplay) {
            //发送数据包
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeItemStack(nowItemDisplay);
                buf.writeBlockPos(pos);
                ServerPlayNetworking.send(player, TRANSPORT_ENTITY_PACKET_ID, buf);
            }
            if (debugEntity == entity) ModLog.LOGGER.info("sending item packet");
            entity.lastItemToDisplay = nowItemDisplay;
        } else {
            //为空
            if (nowTime - entity.lastEmptyTick > 20 && entity.lastItemToDisplay != ItemStack.EMPTY) {
                for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeItemStack(nowItemDisplay);
                    buf.writeBlockPos(pos);
                    ServerPlayNetworking.send(player, TRANSPORT_ENTITY_PACKET_ID, buf);
                    entity.lastEmptyTick = world.getTime();
                }
                entity.lastItemToDisplay = ItemStack.EMPTY;
                entity.lastEmptyTick = nowTime;
                if (debugEntity == entity) ModLog.LOGGER.info("sending empty packet");
            }
        }

//        if (debugEntity == entity) ModLog.LOGGER.info("update status...");
        if (CanTransfer(world, pos, state) && !entity.needsCooling()) {
            entity.setCooling(0);
//            if (debugEntity == entity) ModLog.LOGGER.info("start insert adn extract");
            insertAndExtract(world, pos, state, entity, () -> TransportPipeEntity.extract(world, entity));
        }
    }

    public static void client_tick(World world, BlockPos pos, BlockState state, TransportPipeEntity entity) {

    }

    private boolean needsCooling() {
        return this.cooling > 0;
    }

    private static boolean extract(World world, TransportPipeEntity entity) {
        Inventory inventory = getInputInventory(world, entity);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            if (isInventoryEmpty(inventory, direction)) {
                return false;
            }
            return getAvailableSlots(inventory, direction).anyMatch(slot -> TransportPipeEntity.extract(entity, inventory, slot, direction));
        }
//        for (ItemEntity itemEntity : TransportCombinerBlockEntity.getInputItemEntities(world, entity)) {
//            if (!TransportCombinerBlockEntity.extract(entity, itemEntity)) continue;
//            return true;
//        }
        return false;
    }

    private static boolean extract(TransportPipeEntity entity, Inventory inventory, int slot, Direction side) {
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

    private static boolean canExtract(TransportPipeEntity entity, Inventory fromInventory, ItemStack itemStack, int slot, Direction side) {
        SidedInventory sidedInventory;
        if (!fromInventory.canTransferTo(entity, slot, itemStack)) {
            return false;
        }
        return !(fromInventory instanceof SidedInventory) || (sidedInventory = (SidedInventory) fromInventory).canExtract(slot, itemStack, side);
    }

    private static boolean insertAndExtract(World world, BlockPos pos, BlockState state, TransportPipeEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return false;
        }
        if (!blockEntity.needsCooling()) {
            boolean bl = false;
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

    private static boolean insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        Inventory inventory2 = getOutputInventory(world, pos, state);
        if (inventory2 == null) {
            if (((TransportPipeEntity) world.getBlockEntity(pos)) == debugEntity) {
                ModLog.LOGGER.info("output inventory no found");
            }
            return false;
        }


        Direction direction = ((TransportPipeEntity) Objects.requireNonNull(world.getBlockEntity(pos))).to.getOpposite();
        if (isInventoryFull(inventory2, direction)) {
            if (((TransportPipeEntity) world.getBlockEntity(pos)) == debugEntity) {
                ModLog.LOGGER.info("output inventory is full");
            }
            return false;
        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) continue;
            ItemStack stack1 = inventory.getStack(i).copy();
            ItemStack stack2 = transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
            if (stack2.isEmpty()) {
                inventory2.markDirty();
                return true;
            }
            inventory.setStack(i, stack1);
        }

        return false;
    }

    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory) inventory).getAvailableSlots(side));
        }
        return IntStream.range(0, inventory.size());
    }

    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        return getAvailableSlots(inventory, direction).allMatch(slot -> {
            ItemStack itemStack = inventory.getStack(slot);
            return itemStack.getCount() >= itemStack.getMaxCount();
        });
    }

    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return getAvailableSlots(inv, facing).allMatch(slot -> inv.getStack(slot).isEmpty());
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
                TransportPipeEntity entity;
                if (bl2 && to instanceof TransportPipeEntity && !(entity = (TransportPipeEntity) to).isDisabled()) {
                    j = 0;
                    if (from instanceof TransportPipeEntity) {
                        TransportPipeEntity entity1 = (TransportPipeEntity) from;
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

    private void setCooling(int cooling) {
        this.cooling = cooling;
    }

    public boolean isDisabled() {
        return this.cooling > MAX_COOLING;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public ItemStack removeStack(int slot) {
        checkLootInteraction(null);
        return items.remove(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        checkLootInteraction(null);
        return Inventories.splitStack(items, slot, amount);
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return items;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.items = list;
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
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return dir == from;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == to;
    }

    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        return getInventoryByDirection(world, pos, ((TransportPipeEntity) world.getBlockEntity(pos)).to);
    }

    private static Inventory getInputInventory(World world, BlockPos pos, BlockState state) {
        return getInventoryByDirection(world, pos, ((TransportPipeEntity) world.getBlockEntity(pos)).from);
    }

    private static Inventory getInputInventory(World world, TransportPipeEntity entity) {
        BlockPos pos = entity.getPos();
        BlockEntity entity1 = world.getBlockEntity(pos.offset(entity.from));
        return (Inventory) entity1;
    }

    private static Inventory getInventoryByDirection(World world, BlockPos pos, Direction direction) {
        return (Inventory) world.getBlockEntity(posMove(pos, direction));
    }

    public static boolean CanTransfer(World world, BlockPos pos, BlockState state) {
        if (!(world.getBlockEntity(pos) instanceof TransportPipeEntity)) {
            return false;
        }
        if (state.get(TransportPipeBlock.PIPE_SHAPE) < 7) return false;
        TransportPipeEntity entity = (TransportPipeEntity) world.getBlockEntity(pos);
        assert entity != null;
        if (!entity.findTransferDirection) return false;
        return true;
    }

    private void updateState(World world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            Integer shape = state.get(TransportPipeBlock.PIPE_SHAPE);
            if (shape < 7) {
                findTransferDirection = false;
                from = null;
                to = null;
//                ModLog.LOGGER.info("SHAPE < 7");
                return;
            }

            Pair<Direction> pair = getDirectionPair(shape);
            BlockPos newPos = posMove(pos, pair.var1);
//            ModLog.LOGGER.info("方块位置:" + pos.toShortString());
//            ModLog.LOGGER.info("搜索方块位置:" + newPos.toShortString() + " 方向: " + pair.var1.toString());
            if (world.getBlockEntity(newPos) instanceof TransportPipeEntity) {
//                ModLog.LOGGER.info("找到管道");
                TransportPipeEntity entity = ((TransportPipeEntity) world.getBlockEntity(newPos));
                if (entity.findTransferDirection && entity.to.getOpposite() == pair.var1) {
                    findTransferDirection = true;
                    from = pair.var1;
                    to = pair.var2;
                    return;
                }
//                ModLog.LOGGER.info("该管道无法传输");
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
            newPos = posMove(pos, pair.var2);
//            ModLog.LOGGER.info("搜索方块位置:" + newPos.toShortString() + " 方向: " + pair.var2.toString());
            if (world.getBlockEntity(newPos) instanceof TransportPipeEntity) {
                TransportPipeEntity entity = ((TransportPipeEntity) world.getBlockEntity(newPos));
                if (entity.findTransferDirection && entity.to.getOpposite() == pair.var2) {
                    findTransferDirection = true;
                    from = pair.var2;
                    to = pair.var1;
                    return;
                }
//                ModLog.LOGGER.info("方向2未找到上一个管道");
            } else if (world.getBlockEntity(newPos) instanceof TransportCombinerBlockEntity) {
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

    private Pair<Direction> getDirectionPair(int shape) {
        return switch (shape) {
            case 7 -> new Pair<>(Direction.NORTH, Direction.SOUTH);
            case 8 -> new Pair<>(Direction.WEST, Direction.EAST);
            case 9 -> new Pair<>(Direction.UP, Direction.DOWN);
            case 10 -> new Pair<>(Direction.NORTH, Direction.WEST);
            case 11 -> new Pair<>(Direction.NORTH, Direction.EAST);
            case 12 -> new Pair<>(Direction.NORTH, Direction.UP);
            case 13 -> new Pair<>(Direction.NORTH, Direction.DOWN);
            case 14 -> new Pair<>(Direction.SOUTH, Direction.WEST);
            case 15 -> new Pair<>(Direction.SOUTH, Direction.EAST);
            case 16 -> new Pair<>(Direction.SOUTH, Direction.UP);
            case 17 -> new Pair<>(Direction.SOUTH, Direction.DOWN);
            case 18 -> new Pair<>(Direction.UP, Direction.WEST);
            case 19 -> new Pair<>(Direction.UP, Direction.EAST);
            case 20 -> new Pair<>(Direction.DOWN, Direction.WEST);
            case 21 -> new Pair<>(Direction.DOWN, Direction.EAST);
            default -> null;
        };
    }

    public static BlockPos posMove(BlockPos pos, Direction direction) {
        BlockPos newPos = new BlockPos(pos);
//        ModLog.LOGGER.warn("变换前: " + newPos.toShortString());
        switch (direction) {
            case NORTH -> newPos = newPos.north(1);
            case SOUTH -> newPos = newPos.south(1);
            case WEST -> newPos = newPos.west(1);
            case EAST -> newPos = newPos.east(1);
            case UP -> newPos = newPos.up(1);
            case DOWN -> newPos = newPos.down(1);
            default -> {
                int i = 1;
//                ModLog.LOGGER.error("Always error dir");
            }
        }
//        ModLog.LOGGER.warn("变换后: " + newPos.toShortString());
        return newPos;
    }


    public boolean isFull() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount()) continue;
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new TransportPipeScreenHandler(i, playerInventory, this);
    }
}
