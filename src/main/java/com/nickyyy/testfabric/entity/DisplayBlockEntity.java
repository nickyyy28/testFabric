package com.nickyyy.testfabric.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisplayBlockEntity extends BlockEntity implements BlockEntityInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public int rotation = 0;
    public ItemStack itemToDisplay = ItemStack.EMPTY;

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.DISPLAY_BLOCK_ENTITY, pos, state);
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

    public ItemStack display() {
        return getStack(0).isEmpty() ? ItemStack.EMPTY : getStack(0);
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

    public static void tick(World world, BlockPos pos, BlockState state, DisplayBlockEntity entity) {
        entity.itemToDisplay = entity.getStack(0).isEmpty() ? ItemStack.EMPTY : entity.getStack(0);
    }
}
