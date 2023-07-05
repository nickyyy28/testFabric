package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.TransportCombinerBlock;
import com.nickyyy.testfabric.screen.TransportCombinerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TransportCombinerBlockEntity extends BlockEntity implements BlockEntityInventory, SidedInventory, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    private final BlockState state;

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
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
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

    public static void tick(World world, BlockPos pos, BlockState state, TransportCombinerBlockEntity entity) {

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
