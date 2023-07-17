package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.entity.PipeFilterBlockEntity;
import com.nickyyy.testfabric.entity.TransportCombinerBlockEntity;
import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PipeFilterBlock extends BlockWithEntity {

    /**
     *  0 南北朝向 西
     *  1 南北朝向 下
     *  2 南北朝向 东
     *  3 南北朝向 上
     *  4 西东朝向 北
     *  5 西东朝向 下
     *  6 西东朝向 南
     *  7 西东朝向 上
     */
    public static final IntProperty FILTER_SHAPE = IntProperty.of("filter_shape", 0, 7);

    public static final VoxelShape NORTH_SOUTH_SHAPE = VoxelShapes.cuboid(0.1875, 0.1875, 0.0f, 0.8125, 0.8125, 1.0f);
    public static final VoxelShape WEST_EAST_SHAPE = VoxelShapes.cuboid(0, 0.1875, 0.1875, 1.0f, 0.8125, 0.8125);

    public static final VoxelShape CENTER_WEST_SHAPE = VoxelShapes.cuboid(0, 0.1875, 0.1875, 0.8125, 0.8125, 0.8125);
    public static final VoxelShape CENTER_EAST_SHAPE = VoxelShapes.cuboid(0.1875, 0.1875, 0.1875, 1.0, 0.8125, 0.8125);
    public static final VoxelShape CENTER_NORTH_SHAPE = VoxelShapes.cuboid(0.1875, 0.1875, 0, 0.8125, 0.8125, 0.8125);
    public static final VoxelShape CENTER_SOUTH_SHAPE = VoxelShapes.cuboid(0.1875, 0.1875, 0.1875, 0.8125, 0.8125, 1.0);
    public static final VoxelShape CENTER_UP_SHAPE = VoxelShapes.cuboid(0.1875, 0.1875, 0.1875, 0.8125, 1.0, 0.8125);
    public static final VoxelShape CENTER_DOWN_SHAPE = VoxelShapes.cuboid(0.1875, 0, 0.1875, 0.8125, 0.8125, 0.8125);

    public static final VoxelShape NORTH_SOUTH_WEST_SHAPE = VoxelShapes.union(NORTH_SOUTH_SHAPE, CENTER_WEST_SHAPE);
    public static final VoxelShape NORTH_SOUTH_EAST_SHAPE = VoxelShapes.union(NORTH_SOUTH_SHAPE, CENTER_EAST_SHAPE);
    public static final VoxelShape NORTH_SOUTH_UP_SHAPE = VoxelShapes.union(NORTH_SOUTH_SHAPE, CENTER_UP_SHAPE);
    public static final VoxelShape NORTH_SOUTH_DOWN_SHAPE = VoxelShapes.union(NORTH_SOUTH_SHAPE, CENTER_DOWN_SHAPE);
    public static final VoxelShape WEST_EAST_NORTH_SHAPE = VoxelShapes.union(WEST_EAST_SHAPE, CENTER_NORTH_SHAPE);
    public static final VoxelShape WEST_EAST_SOUTH_SHAPE = VoxelShapes.union(WEST_EAST_SHAPE, CENTER_SOUTH_SHAPE);
    public static final VoxelShape WEST_EAST_UP_SHAPE = VoxelShapes.union(WEST_EAST_SHAPE, CENTER_UP_SHAPE);
    public static final VoxelShape WEST_EAST_DOWN_SHAPE = VoxelShapes.union(WEST_EAST_SHAPE, CENTER_DOWN_SHAPE);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FILTER_SHAPE)){
            case 0: return NORTH_SOUTH_WEST_SHAPE;
            case 1: return NORTH_SOUTH_DOWN_SHAPE;
            case 2: return NORTH_SOUTH_EAST_SHAPE;
            case 3: return NORTH_SOUTH_UP_SHAPE;
            case 4: return WEST_EAST_NORTH_SHAPE;
            case 5: return WEST_EAST_DOWN_SHAPE;
            case 6: return WEST_EAST_SOUTH_SHAPE;
            case 7: return WEST_EAST_UP_SHAPE;
            default: return NORTH_SOUTH_WEST_SHAPE;
        }
    }

    protected PipeFilterBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FILTER_SHAPE, 0));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeFilterBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FILTER_SHAPE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FILTER_SHAPE, getShapeByLookAndAround(ctx.getPlayer().getHorizontalFacing(), ctx.getWorld(), ctx.getBlockPos()));
    }

    private int getShapeByLookAndAround(Direction direction, World world, BlockPos pos) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.west()).getBlock())) {
                return 0;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.down()).getBlock())) {
                return 1;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.east()).getBlock())) {
                return 2;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.up()).getBlock())) {
                return 3;
            }
            return 0;
        } else if (direction == Direction.WEST || direction == Direction.EAST) {
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.north()).getBlock())) {
                return 4;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.down()).getBlock())) {
                return 5;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.south()).getBlock())) {
                return 6;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.up()).getBlock())) {
                return 7;
            }
            return 4;
        }
        return 0;
    }

    private int getShapeByLookAndAround(Direction direction, WorldAccess world, BlockPos pos) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.west()).getBlock())) {
                return 0;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.down()).getBlock())) {
                return 1;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.east()).getBlock())) {
                return 2;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.up()).getBlock())) {
                return 3;
            }
            return 0;
        } else if (direction == Direction.WEST || direction == Direction.EAST) {
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.north()).getBlock())) {
                return 4;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.down()).getBlock())) {
                return 5;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.south()).getBlock())) {
                return 6;
            }
            if (TransportPipeBlock.similarWith(world.getBlockState(pos.up()).getBlock())) {
                return 7;
            }
            return 4;
        }
        return 0;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            state.with(FILTER_SHAPE, getShapeByLookAndAround(placer.getHorizontalFacing(), world, pos));
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient()) {
            return state.with(FILTER_SHAPE, getShapeByLookAndAround(state.get(FILTER_SHAPE) <= 3 ? Direction.NORTH : Direction.WEST, world, pos));
//            ModLog.LOGGER.info("update state");
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PipeFilterBlockEntity) {
                ItemScatterer.spawn(world, pos, (PipeFilterBlockEntity)blockEntity);
                // 更新比较器
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.isOf(state.getBlock())) return;

        world.updateNeighbor(state, pos, Blocks.AIR, pos, false);
        super.onBlockAdded(state, world, pos, oldState, false);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient) return checkType(type, ModEntities.PIPE_FILTER_ENTITY, PipeFilterBlockEntity::server_tick);
        return null;
    }
}
