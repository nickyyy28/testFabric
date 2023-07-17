package com.nickyyy.testfabric;

import com.nickyyy.testfabric.block.ModBlocks;
import com.nickyyy.testfabric.block.TransportPipeBlock;
import com.nickyyy.testfabric.entity.ModEntities;
import com.nickyyy.testfabric.item.ModItemGroup;
import com.nickyyy.testfabric.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ModMain implements ModInitializer {

    //    public static final Item FRUIT_STRAWBERRY = new Item(new Item.Settings().food(ModFoodComponents.FRUIT_STRAWBERRY));

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {

        registerItems();
        registerBlocks();
        registerEntities();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRANSPORT_PIPE_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISPLAY_BLOCK, RenderLayer.getTranslucent());

        Registry.register(Registries.ITEM_GROUP, ModItemGroup.MOD_ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.STRAWBERRY))
                .displayName(Text.translatable("item_group.testfabric.mod_item_tab"))
                .build());

//        ScreenRegistry.register(ModScreenHandlers.TRANSPORT_COMBINER_SCREEN_HANDLER, TransportCombinerScreen::new);




        CompostingChanceRegistry.INSTANCE.add(ModItems.STRAWBERRY, 300.0F);
        ItemGroupEvents.modifyEntriesEvent(ModItemGroup.MOD_ITEM_GROUP).register((context) -> {
            context.add(ModItems.STRAWBERRY);
            context.add(ModItems.HORN);
            context.add(ModItems.REDSTONE_TRANSFORM_ENGINE);
            context.add(ModItems.MINING_MACHINE);
            context.add(ModItems.VERTICAL_HALF_BRICK);
            context.add(ModItems.TRANSPORT_PIPE);
            context.add(ModItems.DISPLAY_BLOCK);
            context.add(ModItems.TRANSPORT_COMBINER);
            context.add(ModItems.PIPE_FILTER);
            context.add(ModItems.STEEL_MATERIAL);
            context.add(ModItems.STEEL_HELMET);
            context.add(ModItems.STEEL_CHESTPLATE);
            context.add(ModItems.STEEL_LEGGINGS);
            context.add(ModItems.STEEL_BOOTS);
        });

        TransportPipeBlock.SIMILAR_BLOCK_SET.addAll(Stream.of(ModBlocks.TRANSPORT_PIPE_BLOCK, ModBlocks.TRANSPORT_COMBINER_BLOCK, ModBlocks.PIPE_FILTER_BLOCK).collect(Collectors.toCollection(ArrayList::new)));
    }

    public void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("testfabric", "strawberry"), ModItems.STRAWBERRY);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "horn"), ModItems.HORN);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "redstone_transform_engine"), ModItems.REDSTONE_TRANSFORM_ENGINE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "mining_machine"), ModItems.MINING_MACHINE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "vertical_half_brick"), ModItems.VERTICAL_HALF_BRICK);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "transport_pipe"), ModItems.TRANSPORT_PIPE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "transport_combiner"), ModItems.TRANSPORT_COMBINER);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "display_block"), ModItems.DISPLAY_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "pipe_filter"), ModItems.PIPE_FILTER);

        Registry.register(Registries.ITEM, new Identifier("testfabric", "steel_ingot"), ModItems.STEEL_MATERIAL);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "steel_helmet"), ModItems.STEEL_HELMET);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "steel_chestplate"), ModItems.STEEL_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "steel_leggings"), ModItems.STEEL_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier("testfabric", "steel_boots"), ModItems.STEEL_BOOTS);
    }

    public void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "redstone_transform_engine"), ModBlocks.REDSTONE_TRANSFORM_ENGINE);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "mining_machine"), ModBlocks.MINING_MACHINE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "vertical_half_brick"), ModBlocks.VERTICAL_HALF_BRICK_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "transport_pipe"), ModBlocks.TRANSPORT_PIPE_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "transport_combiner"), ModBlocks.TRANSPORT_COMBINER_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "display_block"), ModBlocks.DISPLAY_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("testfabric", "pipe_filter"), ModBlocks.PIPE_FILTER_BLOCK);
    }

    public void registerEntities() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "mining_machine_entity"),
                ModEntities.MINING_MACHINE_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "display_block_entity"),
                ModEntities.DISPLAY_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "transport_pipe_entity"),
                ModEntities.TRANSPORT_PIPE_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "transport_combiner_entity"),
                ModEntities.TRANSPORT_COMBINER_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("testfabric", "pipe_filter"),
                ModEntities.PIPE_FILTER_ENTITY);
    }
}
