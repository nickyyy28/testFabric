package com.nickyyy.testfabric.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShredderBlock extends AbstractWorkBlock{
    protected ShredderBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void openScreen(World var1, BlockPos var2, PlayerEntity var3) {

    }

    @Override
    protected boolean canWork() {
        return true;
    }

    @Override
    protected <E extends BlockEntity> BlockEntityType<E> getEntityType() {
        return null;
    }

    @Override
    protected <E extends BlockEntity> BlockEntityTicker<? super E> ticker() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
