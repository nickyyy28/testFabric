package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.entity.PulverizerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PulverizerBlock extends AbstractWorkBlock{
    protected PulverizerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canWork() {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PulverizerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        return checkType(type, ModEntities.PULVERIZER_BLOCK_ENTITY, PulverizerBlockEntity::server_tick);
    }
}
