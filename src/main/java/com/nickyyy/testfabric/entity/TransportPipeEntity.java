package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.TransportCombinerBlock;
import com.nickyyy.testfabric.block.TransportPipeBlock;
import com.nickyyy.testfabric.util.ModLog;
import com.nickyyy.testfabric.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TransportPipeEntity extends LootableContainerBlockEntity implements BlockEntityInventory, SidedInventory {
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public ItemStack itemToDisplay = ItemStack.EMPTY;

    public Direction from = null;
    public Direction to = null;
    public boolean findTransferDirection = false;

    public TransportPipeEntity(BlockPos pos, BlockState state) {
        super(ModEntities.TRANSPORT_PIPE_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
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

    public static void tick(World world, BlockPos pos, BlockState state, TransportPipeEntity entity) {
        entity.updateState(world, pos, state);
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
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {

        return null;
    }

    private static Inventory getInputInventory(World world, BlockPos pos, BlockState state) {
        return null;
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
        Integer shape = state.get(TransportPipeBlock.PIPE_SHAPE);
        if (shape < 7) {
            findTransferDirection = false;
            from = null;
            to = null;
            ModLog.LOGGER.info("SHAPE < 7");
            return;
        }
        Pair<Direction> pair = getDirectionPair(shape);
        BlockPos newPos = posMove(pos, pair.var1);
        if (world.getBlockEntity(newPos) instanceof TransportPipeEntity) {
            TransportPipeEntity entity = ((TransportPipeEntity) world.getBlockEntity(newPos));
            if (entity.findTransferDirection && entity.to.getOpposite() == pair.var1) {
                findTransferDirection = true;
                from = pair.var1;
                to = pair.var2;
                return;
            }
            ModLog.LOGGER.info("方向1未找到上一个管道");
        } else if (world.getBlockEntity(newPos) instanceof TransportCombinerBlockEntity) {
            TransportCombinerBlockEntity entity = (TransportCombinerBlockEntity) world.getBlockEntity(newPos);
            BlockState state1 = world.getBlockState(newPos);
            if (TransportCombinerBlock.getDirectionByState(state1.get(TransportCombinerBlock.FACING)) == pair.var1) {
                findTransferDirection = true;
                from = pair.var1;
                to = pair.var2;
                return;
            }
            ModLog.LOGGER.info("方向1未找到合流器");
        }
        newPos = posMove(pos, pair.var2);
        if (world.getBlockEntity(newPos) instanceof TransportPipeEntity) {
            TransportPipeEntity entity = ((TransportPipeEntity) world.getBlockEntity(newPos));
            if (entity.findTransferDirection && entity.to.getOpposite() == pair.var2) {
                findTransferDirection = true;
                from = pair.var2;
                to = pair.var1;
                return;
            }
            ModLog.LOGGER.info("方向2未找到上一个管道");
        } else if (world.getBlockEntity(newPos) instanceof TransportCombinerBlockEntity) {
            TransportCombinerBlockEntity entity = (TransportCombinerBlockEntity) world.getBlockEntity(newPos);
            BlockState state1 = world.getBlockState(newPos);
            if (TransportCombinerBlock.getDirectionByState(state1.get(TransportCombinerBlock.FACING)) == pair.var2) {
                findTransferDirection = true;
                from = pair.var2;
                to = pair.var1;
                return;
            }
            ModLog.LOGGER.info("方向2未找到合流器");
        }

        findTransferDirection = false;
        from = null;
        to = null;
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
        switch (direction) {
            case NORTH -> newPos.north();
            case SOUTH -> newPos.south();
            case WEST -> newPos.west();
            case EAST -> newPos.east();
            case UP -> newPos.up();
            case DOWN -> newPos.down();
            default -> {
                int i = 1;
            }
        }
        return newPos;
    }
}
