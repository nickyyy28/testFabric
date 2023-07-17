package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.PipeFilterBlock;
import com.nickyyy.testfabric.screen.PipeFilterScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
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

public class PipeFilterBlockEntity extends LootableContainerBlockEntity implements BlockEntityInventory, SidedInventory, ExtendedScreenHandlerFactory {
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(15, ItemStack.EMPTY);

    public static final int MAX_COOLING = 4;

    private int cooling = -1;

    private boolean findTransferDirection = false;

    private Direction from = null, to = null, otherTo = null;

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
        int[] result = new int[getItems().size() - 6];
        for (int i = 0 ; i < result.length ; i++) {
//            ItemStack itemStack = items.get(i);
//            boolean isFiltered = false;
//            Item item = itemStack.getItem();
//            for (int filterItem = 9 ; filterItem < items.size() ; ++filterItem) {
//                if (items.get(filterItem) == itemStack) {
//
//                }
//            }
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
        return dir == to || dir == otherTo;
    }

    public static void server_tick(World world, BlockPos pos, BlockState state, PipeFilterBlockEntity entity) {

    }

    private static Inventory getOutputInventory(World world, BlockPos pos, BlockState state) {
        return TransportPipeBlockEntity.getInventoryByDirection(world, pos, ((PipeFilterBlockEntity) world.getBlockEntity(pos)).to);
    }

    private static Inventory getOtherOutputInventory(World world, BlockPos pos, BlockState state) {
        return TransportPipeBlockEntity.getInventoryByDirection(world, pos, ((PipeFilterBlockEntity) world.getBlockEntity(pos)).otherTo);
    }

    private static Inventory getInputInventory(World world, BlockPos pos, BlockState state) {
        return TransportPipeBlockEntity.getInventoryByDirection(world, pos, ((PipeFilterBlockEntity) world.getBlockEntity(pos)).from);
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
