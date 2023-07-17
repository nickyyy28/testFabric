package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.MiningMachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {
    public static final RedStoneTransformEngineBlock REDSTONE_TRANSFORM_ENGINE = new RedStoneTransformEngineBlock(FabricBlockSettings.create().hardness(4.0f));
    public static final MiningMachineBlock MINING_MACHINE_BLOCK = new MiningMachineBlock(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE));
    public static final VerticalHalfBrickBlock VERTICAL_HALF_BRICK_BLOCK = new VerticalHalfBrickBlock(FabricBlockSettings.create().hardness(3.0f));
    public static final TransportPipeBlock TRANSPORT_PIPE_BLOCK = new TransportPipeBlock(FabricBlockSettings.create().hardness(4.0f));
    public static final DisplayBlock DISPLAY_BLOCK = new DisplayBlock(FabricBlockSettings.create().hardness(4.0f));
    public static final TransportCombinerBlock TRANSPORT_COMBINER_BLOCK = new TransportCombinerBlock(FabricBlockSettings.create().hardness(4.0f));
    public static final PipeFilterBlock PIPE_FILTER_BLOCK = new PipeFilterBlock(FabricBlockSettings.create().hardness(4.0f));
}
