package com.nickyyy.testfabric.entity;

import com.nickyyy.testfabric.block.MiningMachineBlock;
import com.nickyyy.testfabric.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;

public class ModEntities {

    public static final BlockEntityType<MiningMachineBlockEntity> MINING_MACHINE_BLOCK_ENTITY =
            FabricBlockEntityTypeBuilder.create(MiningMachineBlockEntity::new, ModBlocks.MINING_MACHINE_BLOCK).build();

    public static final BlockEntityType<DisplayBlockEntity> DISPLAY_BLOCK_ENTITY =
            FabricBlockEntityTypeBuilder.create(DisplayBlockEntity::new, ModBlocks.DISPLAY_BLOCK).build();

    public static final BlockEntityType<TransportPipeEntity> TRANSPORT_PIPE_ENTITY =
            FabricBlockEntityTypeBuilder.create(TransportPipeEntity::new, ModBlocks.TRANSPORT_PIPE_BLOCK).build();

    public static final BlockEntityType<TransportCombinerBlockEntity> TRANSPORT_COMBINER_BLOCK_ENTITY =
            FabricBlockEntityTypeBuilder.create(TransportCombinerBlockEntity::new, ModBlocks.TRANSPORT_COMBINER_BLOCK).build();
}
