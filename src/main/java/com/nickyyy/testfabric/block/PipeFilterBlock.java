package com.nickyyy.testfabric.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PipeFilterBlock extends BlockWithEntity {

    public static final IntProperty FILTER_SHAPE = IntProperty.of("filter_shape", 0, 8);


    protected PipeFilterBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FILTER_SHAPE, 0));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FILTER_SHAPE);
    }
}
