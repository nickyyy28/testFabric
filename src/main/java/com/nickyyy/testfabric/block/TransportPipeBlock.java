package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TransportPipeBlock extends Block {

    public static final VoxelShape BASIC_SHAPE = VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);

    public static final VoxelShape HALFCONN_NORTH_SHAPE = VoxelShapes.cuboid(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 0.75f);
    public static final VoxelShape HALFCONN_EAST_SHAPE = VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
    public static final VoxelShape HALFCONN_SOUTH_SHAPE = VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 1.0f);
    public static final VoxelShape HALFCONN_WEST_SHAPE = VoxelShapes.cuboid(0.0, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
    public static final VoxelShape HALFCONN_UP_SHAPE = VoxelShapes.cuboid(0.25f, 0.25f, 0.25f, 0.75f, 1.0f, 0.75f);
    public static final VoxelShape HALFCONN_DOWN_SHAPE = VoxelShapes.cuboid(0.25f, 0.0, 0.25f, 0.75f, 0.75f, 0.75f);
    public static final VoxelShape FULLCONN_Z_AXIS_SHAPE = VoxelShapes.cuboid(0.25f, 0.25f, 0.0f, 0.75f, 0.75f, 1.0f);
    public static final VoxelShape FULLCONN_X_AXIS_SHAPE = VoxelShapes.cuboid(0.0f, 0.25f, 0.25f, 1.0f, 0.75f, 0.75f);
    public static final VoxelShape FULLCONN_Y_AXIS_SHAPE = VoxelShapes.cuboid(0.25f, 0.0f, 0.25f, 0.75f, 1.0f, 0.75f);
    public static final VoxelShape CORNER_NORTH_WEST_SHAPE = VoxelShapes.union(HALFCONN_NORTH_SHAPE, HALFCONN_WEST_SHAPE);
    public static final VoxelShape CORNER_NORTH_EAST_SHAPE = VoxelShapes.union(HALFCONN_NORTH_SHAPE, HALFCONN_EAST_SHAPE);
    public static final VoxelShape CORNER_NORTH_UP_SHAPE = VoxelShapes.union(HALFCONN_NORTH_SHAPE, HALFCONN_UP_SHAPE);
    public static final VoxelShape CORNER_NORTH_DOWN_SHAPE = VoxelShapes.union(HALFCONN_NORTH_SHAPE, HALFCONN_DOWN_SHAPE);

    public static final VoxelShape CORNER_SOUTH_WEST_SHAPE = VoxelShapes.union(HALFCONN_SOUTH_SHAPE, HALFCONN_WEST_SHAPE);
    public static final VoxelShape CORNER_SOUTH_EAST_SHAPE = VoxelShapes.union(HALFCONN_SOUTH_SHAPE, HALFCONN_EAST_SHAPE);
    public static final VoxelShape CORNER_SOUTH_UP_SHAPE = VoxelShapes.union(HALFCONN_SOUTH_SHAPE, HALFCONN_UP_SHAPE);
    public static final VoxelShape CORNER_SOUTH_DOWN_SHAPE = VoxelShapes.union(HALFCONN_SOUTH_SHAPE, HALFCONN_DOWN_SHAPE);

    public static final VoxelShape CORNER_UP_WEST_SHAPE = VoxelShapes.union(HALFCONN_UP_SHAPE, HALFCONN_WEST_SHAPE);
    public static final VoxelShape CORNER_UP_EAST_SHAPE = VoxelShapes.union(HALFCONN_UP_SHAPE, HALFCONN_EAST_SHAPE);
    public static final VoxelShape CORNER_DOWN_WEST_SHAPE = VoxelShapes.union(HALFCONN_DOWN_SHAPE, HALFCONN_WEST_SHAPE);
    public static final VoxelShape CORNER_DOWN_EAST_SHAPE = VoxelShapes.union(HALFCONN_DOWN_SHAPE, HALFCONN_EAST_SHAPE);

    public static final IntProperty PIPE_SHAPE = IntProperty.of("pipe_shape", 0, 21);

    public TransportPipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(PIPE_SHAPE, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(PIPE_SHAPE)) {
            case 0 -> BASIC_SHAPE;
            case 1 -> HALFCONN_NORTH_SHAPE;
            case 2 -> HALFCONN_EAST_SHAPE;
            case 3 -> HALFCONN_SOUTH_SHAPE;
            case 4 -> HALFCONN_WEST_SHAPE;
            case 5 -> HALFCONN_UP_SHAPE;
            case 6 -> HALFCONN_DOWN_SHAPE;
            case 7 -> FULLCONN_Z_AXIS_SHAPE;
            case 8 -> FULLCONN_X_AXIS_SHAPE;
            case 9 -> FULLCONN_Y_AXIS_SHAPE;
            case 10 -> CORNER_NORTH_WEST_SHAPE;
            case 11 -> CORNER_NORTH_EAST_SHAPE;
            case 12 -> CORNER_NORTH_UP_SHAPE;
            case 13 -> CORNER_NORTH_DOWN_SHAPE;
            case 14 -> CORNER_SOUTH_WEST_SHAPE;
            case 15 -> CORNER_SOUTH_EAST_SHAPE;
            case 16 -> CORNER_SOUTH_UP_SHAPE;
            case 17 -> CORNER_SOUTH_DOWN_SHAPE;
            case 18 -> CORNER_UP_WEST_SHAPE;
            case 19 -> CORNER_UP_EAST_SHAPE;
            case 20 -> CORNER_DOWN_WEST_SHAPE;
            case 21 -> CORNER_DOWN_EAST_SHAPE;
            default -> BASIC_SHAPE;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PIPE_SHAPE);
    }

    private int directionMatch(Direction dir1, Direction dir2) {
        if (dir1 == Direction.NORTH) {
            if (dir2 == Direction.WEST) return 10;
            if (dir2 == Direction.EAST) return 11;
            if (dir2 == Direction.UP) return 12;
            if (dir2 == Direction.DOWN) return 13;
        } else if (dir1 == Direction.SOUTH) {
            if (dir2 == Direction.WEST) return 14;
            if (dir2 == Direction.EAST) return 15;
            if (dir2 == Direction.UP) return 16;
            if (dir2 == Direction.DOWN) return 17;
        } else if (dir1 == Direction.UP) {
            if (dir2 == Direction.WEST) return 18;
            if (dir2 == Direction.EAST) return 19;
        } else if (dir1 == Direction.DOWN) {
            if (dir2 == Direction.WEST) return 20;
            if (dir2 == Direction.EAST) return 21;
        }

        if (dir2 == Direction.NORTH) {
            if (dir1 == Direction.WEST) return 10;
            if (dir1 == Direction.EAST) return 11;
            if (dir1 == Direction.UP) return 12;
            if (dir1 == Direction.DOWN) return 13;
        } else if (dir2 == Direction.SOUTH) {
            if (dir1 == Direction.WEST) return 14;
            if (dir1 == Direction.EAST) return 15;
            if (dir1 == Direction.UP) return 16;
            if (dir1 == Direction.DOWN) return 17;
        } else if (dir2 == Direction.UP) {
            if (dir1 == Direction.WEST) return 18;
            if (dir1 == Direction.EAST) return 19;
        } else if (dir2 == Direction.DOWN) {
            if (dir1 == Direction.WEST) return 20;
            if (dir1 == Direction.EAST) return 21;
        }

        return 0;
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            Integer integer = state.get(PIPE_SHAPE);

//            ModLog.LOGGER.info("Place POS: " + pos.toShortString());
//            ModLog.LOGGER.info("North POS: " + pos.north().toShortString());
//            ModLog.LOGGER.info("West POS: " + pos.west().toShortString());
//            ModLog.LOGGER.info("South POS: " + pos.south().toShortString());
//            ModLog.LOGGER.info("East POS: " + pos.east().toShortString());

            int model = getModel(world, pos, this);
            state.with(PIPE_SHAPE, model);
        }
    }


    static int ii = 0;

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!world.isClient()) {
            int m = getModel(world, pos);
            return state.with(PIPE_SHAPE, m);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public int getModel(World world, BlockPos pos, Block block) {
        ArrayList<Direction> directions = getAroundSameBlocks(world, pos, this);
        if (directions.size() == 0) {
            ModLog.LOGGER.info("Block Have no around blocks");
            return 0;
        } else if (directions.size() == 1) {
            ModLog.LOGGER.info("Block Have 1 around blocks");
            Direction dir = directions.get(0);
            return switch (dir) {
                case NORTH -> 1;
                case EAST -> 2;
                case SOUTH -> 3;
                case WEST -> 4;
                case UP -> 5;
                case DOWN -> 6;
            };
        } else {
            Direction dir1 = directions.get(0), dir2 = directions.get(1);
            if (isOpposite(dir1, dir2)) {
                ModLog.LOGGER.info("2 block is opposite");
                if (dir1 == Direction.NORTH || dir2 == Direction.NORTH) {
                    return 7;
                } else if (dir1 == Direction.EAST || dir2 == Direction.EAST) {
                    return 8;
                } else if (dir1 == Direction.UP || dir2 == Direction.UP) {
                    return 9;
                }
            } else {
                return directionMatch(dir1, dir2);
            }
            ModLog.LOGGER.info("Block Have " + directions.size() + " around blocks");
            return 0;
        }
    }

    public int getModel(WorldAccess world, BlockPos pos) {
        ArrayList<Direction> directions = getAroundSameBlocks(world, pos, this);

        if (directions.size() == 0) {
            ModLog.LOGGER.info("Block Have no around blocks");
            return 0;
        } else if (directions.size() == 1) {
            ModLog.LOGGER.info("Block Have 1 around blocks");
            Direction dir = directions.get(0);
            return switch (dir) {
                case NORTH -> 1;
                case EAST -> 2;
                case SOUTH -> 3;
                case WEST -> 4;
                case UP -> 5;
                case DOWN -> 6;
            };
        } else {
            Direction dir1 = directions.get(0), dir2 = directions.get(1);
            if (isOpposite(dir1, dir2)) {
                ModLog.LOGGER.info("2 block is opposite");
                if (dir1 == Direction.NORTH || dir2 == Direction.NORTH) {
                    return 7;
                } else if (dir1 == Direction.EAST || dir2 == Direction.EAST) {
                    return 8;
                } else if (dir1 == Direction.UP || dir2 == Direction.UP) {
                    return 9;
                }
            } else {
                return directionMatch(dir1, dir2);
            }
            ModLog.LOGGER.info("Block Have " + directions.size() + " around blocks");
            return 0;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(PIPE_SHAPE, getModel(ctx.getWorld(), ctx.getBlockPos(), this));
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.isOf(state.getBlock())) {
            return;
        }

        world.updateNeighbor(state, pos, Blocks.AIR, pos, false);
        super.onBlockAdded(state, world, pos, oldState, false);
    }

    public static ArrayList<Direction> getAroundSameBlocks(World world, BlockPos pos, Block block) {
        ArrayList<Direction> directions = new ArrayList<>();

        Block north_block = world.getBlockState(pos.north()).getBlock();
        if (north_block == block) directions.add(Direction.NORTH);
        Block west_block = world.getBlockState(pos.west()).getBlock();
        if (west_block == block) directions.add(Direction.WEST);
        Block south_block = world.getBlockState(pos.south()).getBlock();
        if (south_block == block) directions.add(Direction.SOUTH);
        Block east_block = world.getBlockState(pos.east()).getBlock();
        if (east_block == block) directions.add(Direction.EAST);
        Block up_block = world.getBlockState(pos.up()).getBlock();
        if (up_block == block) directions.add(Direction.UP);
        Block down_block = world.getBlockState(pos.down()).getBlock();
        if (down_block == block) directions.add(Direction.DOWN);

        return directions;
    }

    public static ArrayList<Direction> getAroundSameBlocks(WorldAccess world, BlockPos pos, Block block) {
        ArrayList<Direction> directions = new ArrayList<>();

        Block north_block = world.getBlockState(pos.north()).getBlock();
        if (north_block == block) directions.add(Direction.NORTH);
        Block west_block = world.getBlockState(pos.west()).getBlock();
        if (west_block == block) directions.add(Direction.WEST);
        Block south_block = world.getBlockState(pos.south()).getBlock();
        if (south_block == block) directions.add(Direction.SOUTH);
        Block east_block = world.getBlockState(pos.east()).getBlock();
        if (east_block == block) directions.add(Direction.EAST);
        Block up_block = world.getBlockState(pos.up()).getBlock();
        if (up_block == block) directions.add(Direction.UP);
        Block down_block = world.getBlockState(pos.down()).getBlock();
        if (down_block == block) directions.add(Direction.DOWN);

        return directions;
    }

    public static boolean isOpposite(Direction direction1, Direction direction2) {
        return direction1.getOpposite() == direction2;
    }
}
