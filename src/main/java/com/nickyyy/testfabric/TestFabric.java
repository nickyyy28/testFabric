package com.nickyyy.testfabric;

import com.nickyyy.testfabric.block.MiningMachineBlock;
import com.nickyyy.testfabric.block.ModBlocks;
import com.nickyyy.testfabric.entity.MiningMachineBlockEntity;
import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.item.ModItemGroup;
import com.nickyyy.testfabric.item.ModItems;
import com.nickyyy.testfabric.render.DisplayBlockEntityRender;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class TestFabric implements ModInitializer {

    //    public static final Item FRUIT_STRAWBERRY = new Item(new Item.Settings().food(ModFoodComponents.FRUIT_STRAWBERRY));

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier("testfabric", "strawberry"), ModItems.STRAWBERRY);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "horn"), ModItems.HORN);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "redstone_transform_engine"), ModItems.REDSTONE_TRANSFORM_ENGINE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "mining_machine"), ModItems.MINING_MACHINE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "vertical_half_brick"), ModItems.VERTICAL_HALF_BRICK);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "transport_pipe"), ModItems.TRANSPORT_PIPE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "display_block"), ModItems.DISPLAY_BLOCK);


        Registry.register(Registries.BLOCK, new Identifier("testfabric", "redstone_transform_engine"), ModBlocks.REDSTONE_TRANSFORM_ENGINE);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "mining_machine"), ModBlocks.MINING_MACHINE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "vertical_half_brick"), ModBlocks.VERTICAL_HALF_BRICK_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "transport_pipe"), ModBlocks.TRANSPORT_PIPE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "display_block"), ModBlocks.DISPLAY_BLOCK);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRANSPORT_PIPE_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISPLAY_BLOCK, RenderLayer.getTranslucent());

        Registry.register(Registries.ITEM_GROUP, ModItemGroup.MOD_ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.STRAWBERRY))
                .displayName(Text.translatable("item_group.testfabric.mod_item_tab"))
                .build());


        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "mining_machine_entity"),
                ModEntities.MINING_MACHINE_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "display_block_entity"),
                ModEntities.DISPLAY_BLOCK_ENTITY);


        CompostingChanceRegistry.INSTANCE.add(ModItems.STRAWBERRY, 300.0F);
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.MOD_ITEM_GROUP).register((context) -> {
            context.add(ModItems.STRAWBERRY);
            context.add(ModItems.HORN);
            context.add(ModItems.REDSTONE_TRANSFORM_ENGINE);
            context.add(ModItems.MINING_MACHINE);
            context.add(ModItems.VERTICAL_HALF_BRICK);
            context.add(ModItems.TRANSPORT_PIPE);
            context.add(ModItems.DISPLAY_BLOCK);
        });

//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((ctx) -> {
//            ctx.add(ModItems.STRAWBERRY);
//        });
    }
}
