package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.TransportCombinerBlock;
import com.nickyyy.testfabric.screen.TransportCombinerScreenHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TransportCombinerBlockEntity extends BlockEntity implements BlockEntityInventory, SidedInventory, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    private final BlockState state;
    private int cooling = -1;
    private long lastTickTime;

    public static final ArrayList<Direction> inputDirs = Stream.of(Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST, Direction.UP, Direction.DOWN)
            .collect(Collectors.toCollection(ArrayList::new));

    public TransportCombinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.TRANSPORT_COMBINER_BLOCK_ENTITY, pos, state);
        this.state = state;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("cooling", this.cooling);
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    public int getCooling() {
        return cooling;
    }

    public void setCooling(int cooling) {
        this.cooling = cooling;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        this.cooling = nbt.getInt("cooling");
    }

    public int size() {
        return this.items.size();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    public DefaultedList<ItemStack> getInvStackList() {
        return this.items;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
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

    public static void tick(World world, BlockPos pos, BlockState state, TransportCombinerBlockEntity entity) {
        entity.cooling--;
        entity.lastTickTime = world.getTime();
        if (!entity.needCooling()) {
            entity.setCooling(0);
            TransportCombinerBlockEntity.insertAndExtract(world, pos, state, entity, () -> TransportCombinerBlockEntity.extract(world, entity));
        }
    }

    private static boolean extract(World world, TransportCombinerBlockEntity entity) {
        Inventory inventory = TransportCombinerBlockEntity.getInputInventory(world, entity);
        if (inventory != null) {
            Direction direction = Direction.DOWN;
            if (TransportCombinerBlockEntity.isInventoryEmpty(inventory, direction)) {
                return false;
            }
            return TransportCombinerBlockEntity.getAvailableSlots(inventory, direction).anyMatch(slot -> TransportCombinerBlockEntity.extract(entity, inventory, slot, direction));
        }
//        for (ItemEntity itemEntity : TransportCombinerBlockEntity.getInputItemEntities(world, entity)) {
//            if (!TransportCombinerBlockEntity.extract(entity, itemEntity)) continue;
//            return true;
//        }
        return false;
    }

    private static boolean extract(Inventory inventory, ItemEntity itemEntity) {
        boolean bl = false;
        ItemStack itemStack = itemEntity.getStack().copy();
        ItemStack itemStack2 = TransportCombinerBlockEntity.transfer(null, inventory, itemStack, null);
        if (itemStack2.isEmpty()) {
            bl = true;
            itemEntity.discard();
        } else {
            itemEntity.setStack(itemStack2);
        }
        return bl;
    }

    private static boolean extract(TransportCombinerBlockEntity entity, Inventory inventory, int slot, Direction side) {
        ItemStack itemStack = inventory.getStack(slot);
        if (!itemStack.isEmpty() && TransportCombinerBlockEntity.canExtract(entity, inventory, itemStack, slot, side)) {
            ItemStack itemStack2 = itemStack.copy();
            ItemStack itemStack3 = TransportCombinerBlockEntity.transfer(inventory, entity, inventory.removeStack(slot, 1), null);
            if (itemStack3.isEmpty()) {
                inventory.markDirty();
                return true;
            }
            inventory.setStack(slot, itemStack2);
        }
        return false;
    }

    public static List<ItemEntity> getInputItemEntities(World world, TransportCombinerBlockEntity entity) {
//        return entity.getInputAreaShape().getBoundingBoxes().stream().flatMap(box -> world.getEntitiesByClass(ItemEntity.class, box.offset(entity.getHopperX() - 0.5, entity.getHopperY() - 0.5, hopper.getHopperZ() - 0.5), EntityPredicates.VALID_ENTITY).stream()).collect(Collectors.toList());
        return null;
    }

    private static boolean isInventoryEmpty(Inventory inventory, Direction direction) {
        return TransportCombinerBlockEntity.getAvailableSlots(inventory, direction).allMatch(slot -> inventory.getStack(slot).isEmpty());
    }

    private static Inventory getInputInventory(World world, TransportCombinerBlockEntity entity) {
        BlockPos pos = entity.getPos();
        Direction outputDir = TransportCombinerBlock.getDirectionByState(entity.state.get(TransportCombinerBlock.FACING)).getOpposite();
        for (Direction inputDir : inputDirs) {
            if (inputDir == outputDir) continue;
            Inventory inventory = getInventoryAt(world, pos.offset(inputDir));
            if (inventory == null || inventory.isEmpty()) {
                continue;
            } else {
                return inventory;
            }
        }

        return null;
    }

    private static boolean insertAndExtract(World world, BlockPos pos, BlockState state, TransportCombinerBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
        if (world.isClient) {
            return false;
        }
        if (!blockEntity.needCooling()) {
            boolean bl = false;
            if (!blockEntity.isEmpty()) {
                bl = TransportCombinerBlockEntity.insert(world, pos, state, blockEntity);
            }
            if (!blockEntity.isFull()) {
                bl |= booleanSupplier.getAsBoolean();
            }
            if (bl) {
                blockEntity.setCooling(8);
                TransportCombinerBlockEntity.markDirty(world, pos, state);
                return true;
            }
        }
        return false;
    }

    private static boolean insert(World world, BlockPos pos, BlockState state, Inventory inventory) {
        Inventory inventory2 = TransportCombinerBlockEntity.getOutputInventory(world, pos, state);
        if (inventory2 == null) return false;

        Direction direction = TransportCombinerBlock.getDirectionByState(state.get(TransportCombinerBlock.FACING));
        if (TransportCombinerBlockEntity.isInventoryFull(inventory2, direction)) {
            return false;
        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) continue;
            ItemStack stack1 = inventory.getStack(i).copy();
            ItemStack stack2 = TransportCombinerBlockEntity.transfer(inventory, inventory2, inventory.removeStack(i, 1), direction);
            if (stack2.isEmpty()) {
                inventory2.markDirty();
                return true;
            }
            inventory.setStack(i, stack1);
        }

        return false;
    }

    private static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, Direction side) {
        if (to instanceof SidedInventory) {
            SidedInventory sidedInventory = (SidedInventory) to;
            if (side != null) {
                int[] is = sidedInventory.getAvailableSlots(side);
                int i = 0;
                while (i < is.length) {
                    if (stack.isEmpty()) return stack;
                    stack = TransportCombinerBlockEntity.transfer(from, to, stack, is[i], side);
                    ++i;
                }
                return stack;
            }
        }
        int j = to.size();
        int i = 0;
        while (i < j) {
            if (stack.isEmpty()) return stack;
            stack = TransportCombinerBlockEntity.transfer(from, to, stack, i, side);
            ++i;
        }
        return stack;
    }

    private static ItemStack transfer(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side) {
        ItemStack itemStack = to.getStack(slot);
        if (TransportCombinerBlockEntity.canInsert(to, stack, slot, side)) {
            int j;
            boolean bl = false;
            boolean bl2 = to.isEmpty();
            if (itemStack.isEmpty()) {
                to.setStack(slot, stack);
                stack = ItemStack.EMPTY;
                bl = true;
            } else if (TransportCombinerBlockEntity.canMergeItems(itemStack, stack)) {
                int i = stack.getMaxCount() - itemStack.getCount();
                j = Math.min(stack.getCount(), i);
                stack.decrement(j);
                itemStack.increment(j);
                boolean bl3 = bl = j > 0;
            }
            if (bl) {
                TransportCombinerBlockEntity hopperBlockEntity;
                if (bl2 && to instanceof TransportCombinerBlockEntity && !(hopperBlockEntity = (TransportCombinerBlockEntity) to).isDisabled()) {
                    j = 0;
                    if (from instanceof TransportCombinerBlockEntity) {
                        TransportCombinerBlockEntity hopperBlockEntity2 = (TransportCombinerBlockEntity) from;
                        if (hopperBlockEntity.lastTickTime >= hopperBlockEntity2.lastTickTime) {
                            j = 1;
                        }
                    }
                    hopperBlockEntity.setCooling(8 - j);
                }
                to.markDirty();
            }
        }
        return stack;
    }

    private boolean isDisabled() {
        return this.cooling > 8;
    }

    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return first.getCount() <= first.getMaxCount() && ItemStack.canCombine(first, second);
    }

    private static boolean canInsert(Inventory inventory, ItemStack stack, int slot, @Nullable Direction side) {
        SidedInventory sidedInventory;
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        return !(inventory instanceof SidedInventory) || (sidedInventory = (SidedInventory) inventory).canInsert(slot, stack, side);
    }

    private static boolean isInventoryFull(Inventory inventory, Direction direction) {
        return TransportCombinerBlockEntity.getAvailableSlots(inventory, direction).allMatch(slot -> {
            ItemStack stack = inventory.getStack(slot);
            return stack.getCount() >= stack.getMaxCount();
        });
    }

    /**
     * @param inventory 容器
     * @param side      方向
     * @return
     * @detail 获取一个容器可用的位置
     */
    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory) {
            return IntStream.of(((SidedInventory) inventory).getAvailableSlots(side));
        }
        return IntStream.range(0, inventory.size());
    }

    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        Direction direction = TransportCombinerBlock.getDirectionByState(state.get(TransportCombinerBlock.FACING)).getOpposite();
        return TransportCombinerBlockEntity.getInventoryAt(world, pos.offset(direction));
    }

    @Nullable
    public static Inventory getInventoryAt(World world, BlockPos pos) {
        return getInventoryAt(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5);
    }

    @Nullable
    private static Inventory getInventoryAt(World world, double x, double y, double z) {
        List<Entity> list;
        BlockEntity blockEntity;
        Inventory inventory = null;
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof InventoryProvider) {
            inventory = ((InventoryProvider) ((Object) block)).getInventory(blockState, world, blockPos);
        } else if (blockState.hasBlockEntity() && (blockEntity = world.getBlockEntity(blockPos)) instanceof Inventory && (inventory = (Inventory) ((Object) blockEntity)) instanceof ChestBlockEntity && block instanceof ChestBlock) {
            inventory = ChestBlock.getInventory((ChestBlock) block, blockState, world, blockPos, true);
        }
        if (inventory == null && !(list = world.getOtherEntities(null, new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntityPredicates.VALID_INVENTORIES)).isEmpty()) {
            inventory = (Inventory) ((Object) list.get(world.random.nextInt(list.size())));
        }
        return inventory;
    }


    private boolean isFull() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty() && itemStack.getCount() == itemStack.getMaxCount()) continue;
            return false;
        }
        return true;
    }

    public boolean needCooling() {
        return this.cooling > 0;
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
        return TransportCombinerBlock.getDirectionByState(state.get(TransportCombinerBlock.FACING)).getOpposite() != dir;
    }

    private static boolean canExtract(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing) {
        SidedInventory sidedInventory;
        if (!fromInventory.canTransferTo(hopperInventory, slot, stack)) {
            return false;
        }
        return !(fromInventory instanceof SidedInventory) || (sidedInventory = (SidedInventory) fromInventory).canExtract(slot, stack, facing);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return TransportCombinerBlock.getDirectionByState(state.get(TransportCombinerBlock.FACING)).getOpposite() == dir;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TransportCombinerScreenHandler(syncId, playerInventory, this);
    }
}
