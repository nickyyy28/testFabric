package com.nickyyy.testfabric.block;

import com.nickyyy.testfabric.entity.MiningMachineBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {
    public static final RedStoneTransformEngineBlock REDSTONE_TRANSFORM_ENGINE = new RedStoneTransformEngineBlock(FabricBlockSettings.create().hardness(2.0f));
    public static final MiningMachineBlock MINING_MACHINE_BLOCK = new MiningMachineBlock(FabricBlockSettings.copyOf(Blocks.SMOOTH_STONE));
    public static final VerticalHalfBrickBlock VERTICAL_HALF_BRICK_BLOCK = new VerticalHalfBrickBlock(FabricBlockSettings.create().hardness(2.0f));
    public static final TransportPipeBlock TRANSPORT_PIPE_BLOCK = new TransportPipeBlock(FabricBlockSettings.create().hardness(2.0f));
    public static final DisplayBlock DISPLAY_BLOCK = new DisplayBlock(FabricBlockSettings.create().hardness(2.0f));
    public static final TransportCombinerBlock TRANSPORT_COMBINER_BLOCK = new TransportCombinerBlock(FabricBlockSettings.create().hardness(2.0f));
    public static final PipeFilterBlock PIPE_FILTER_BLOCK = new PipeFilterBlock(FabricBlockSettings.create().hardness(2.0f));
    //银矿
    public static final Block SILVER_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLUE).hardness(3.0f).instrument(Instrument.BASEDRUM).requiresTool().strength(1.0f, 1.0f));
    //锡矿
    public static final Block TIN_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(1.0f, 1.0f));
    //锂矿
    public static final Block LITHIUM_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    //钛矿
    public static final Block TITANIUM_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    //铅矿
    public static final Block LEAD_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(4.5f, 4.5f));
    //铝矿
    public static final Block ALUMINIUM_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    //铀矿
    public static final Block URANIUM_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    //磷矿
    public static final Block PHOSPHORUS_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    //硫矿
    public static final Block SULFUR_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    //稀土矿
    public static final Block RARE_EARTH_ORE = new ExperienceDroppingBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).instrument(Instrument.BASEDRUM).hardness(3.0f).requiresTool().strength(3.0f, 3.0f));

    public static final Block PULVERIZER_BLOCK = new PulverizerBlock(FabricBlockSettings.create().hardness(3.0f));
}
