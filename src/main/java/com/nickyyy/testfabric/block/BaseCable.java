package com.nickyyy.testfabric.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

import java.util.Map;
import java.util.function.Function;

public abstract class BaseCable extends BlockWithEntity {

    public static final EnumProperty<WireConnection> CABLE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> CABLE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> CABLE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> CABLE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
//    public static final IntProperty POWER = Properties.POWER;

//    public static final IntProperty CABLE_SHAPE = IntProperty.of("cable_shape", 0, 1);

    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, CABLE_CONNECTION_NORTH, Direction.EAST, CABLE_CONNECTION_EAST, Direction.SOUTH, CABLE_CONNECTION_SOUTH, Direction.WEST, CABLE_CONNECTION_WEST));
    public static final Map<Integer, EnumProperty<WireConnection>> INDEX_TO_WIRE_CONNECTION_PROPERTY = Maps.newHashMap(ImmutableMap.of(0, CABLE_CONNECTION_NORTH, 1, CABLE_CONNECTION_SOUTH, 2, CABLE_CONNECTION_WEST, 3, CABLE_CONNECTION_EAST));
    public static final Map<Integer, WireConnection> INDEX_TO_CONNECTION = Maps.newHashMap(ImmutableMap.of(0, WireConnection.NONE, 1, WireConnection.SIDE, 2, WireConnection.UP));
    public static final VoxelShape BASE_SHAPE = VoxelShapes.cuboid(0.4375f, 0.0f, 0.0f, 0.5625, 0.125f, 1.0f);

    public static final VoxelShape CABLE_DOT_SHAPE = VoxelShapes.cuboid(0.375f, 0.0f, 0.375f, 0.625f, 0.0625f, 0.625f);
    public static final VoxelShape CABLE_SIDE_NORTH_SHAPE = VoxelShapes.cuboid(0.4375f, 0, 0, 0.5625f, 0.0625f, 0.5625f);
    public static final VoxelShape CABLE_SIDE_SOUTH_SHAPE = VoxelShapes.cuboid(0.4375f, 0, 0.4325f, 0.5625f, 0.0625f, 1.0f);
    public static final VoxelShape CABLE_SIDE_WEST_SHAPE = VoxelShapes.cuboid(0, 0, 0.4325f, 0.5625f, 0.0625f, 0.5625f);
    public static final VoxelShape CABLE_SIDE_EAST_SHAPE = VoxelShapes.cuboid(0.4375f, 0, 0.4325f, 1, 0.0625f, 0.5625f);

    public static final VoxelShape CABLE_UP_NORTH_SHAPE = VoxelShapes.cuboid(0.4375f, 0.0f, 0.0f, 0.5625f, 1.0f, 0.0625f);
    public static final VoxelShape CABLE_UP_SOUTH_SHAPE = VoxelShapes.cuboid(0.4375f, 0.0f, 0.9375f, 0.5625f, 1.0f, 1.0f);
    public static final VoxelShape CABLE_UP_WEST_SHAPE = VoxelShapes.cuboid(0.0f, 0.0f, 0.4375f, 0.0625f, 1.0f, 0.5625f);
    public static final VoxelShape CABLE_UP_EAST_SHAPE = VoxelShapes.cuboid(0.9375f, 0.0f, 0.4375f, 1.0f, 1.0f, 0.5625f);

    public static final VoxelShape CABLE_SIDEUP_NORTH_SHAPE = VoxelShapes.union(CABLE_SIDE_NORTH_SHAPE, CABLE_UP_NORTH_SHAPE);
    public static final VoxelShape CABLE_SIDEUP_SOUTH_SHAPE = VoxelShapes.union(CABLE_SIDE_SOUTH_SHAPE, CABLE_UP_SOUTH_SHAPE);
    public static final VoxelShape CABLE_SIDEUP_WEST_SHAPE = VoxelShapes.union(CABLE_SIDE_WEST_SHAPE, CABLE_UP_WEST_SHAPE);
    public static final VoxelShape CABLE_SIDEUP_EAST_SHAPE = VoxelShapes.union(CABLE_SIDE_EAST_SHAPE, CABLE_UP_EAST_SHAPE);

    protected BaseCable(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(CABLE_CONNECTION_WEST, WireConnection.NONE)
                .with(CABLE_CONNECTION_EAST, WireConnection.NONE)
                .with(CABLE_CONNECTION_NORTH, WireConnection.NONE)
                .with(CABLE_CONNECTION_SOUTH, WireConnection.NONE));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;


        int i = Random.create().nextInt(4);
        int j = Random.create().nextInt(3);
        player.sendMessage(Text.of("i = " + i + " j = " + j));
        state.with(INDEX_TO_WIRE_CONNECTION_PROPERTY.get(i), INDEX_TO_CONNECTION.get(j));
        setDefaultState(state);
        world.updateNeighbor(state, pos, this, pos, false);

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        WireConnection connectionNorth = state.get(CABLE_CONNECTION_NORTH);
        WireConnection connectionSouth = state.get(CABLE_CONNECTION_SOUTH);
        WireConnection connectionEast = state.get(CABLE_CONNECTION_EAST);
        WireConnection connectionWest = state.get(CABLE_CONNECTION_WEST);

        if (connectionNorth == WireConnection.NONE && connectionSouth == WireConnection.NONE &&
                connectionEast == WireConnection.NONE && connectionWest == WireConnection.NONE) {
            return CABLE_DOT_SHAPE;
        }

        VoxelShape OUT = VoxelShapes.cuboid(0, 0, 0, 0, 0, 0);
        if (connectionNorth == WireConnection.SIDE) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDE_NORTH_SHAPE);
        } else if (connectionNorth == WireConnection.UP) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDEUP_NORTH_SHAPE);
        }

        if (connectionSouth == WireConnection.SIDE) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDE_SOUTH_SHAPE);
        } else if (connectionSouth == WireConnection.UP) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDEUP_SOUTH_SHAPE);
        }

        if (connectionWest == WireConnection.SIDE) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDE_WEST_SHAPE);
        } else if (connectionWest == WireConnection.UP) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDEUP_WEST_SHAPE);
        }

        if (connectionEast == WireConnection.SIDE) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDE_EAST_SHAPE);
        } else if (connectionEast == WireConnection.UP) {
            OUT = VoxelShapes.union(OUT, CABLE_SIDEUP_EAST_SHAPE);
        }

        return OUT;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        //TODO 改变方块状态
        return super.getPlacementState(ctx);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            //TODO 改变方块状态

        }
    }



    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        //TODO 相邻方块更新，改变自身状态
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.isOf(state.getBlock())) return;
        world.updateNeighbor(state, pos, Blocks.AIR, pos, false);
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CABLE_CONNECTION_NORTH).add(CABLE_CONNECTION_SOUTH).add(CABLE_CONNECTION_WEST).add(CABLE_CONNECTION_EAST);
    }


}
