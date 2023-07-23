package com.nickyyy.testfabric.generator;

import com.nickyyy.testfabric.block.ModBlocks;
import com.nickyyy.testfabric.item.ModItem;
import com.nickyyy.testfabric.item.ModItemGroup;
import com.nickyyy.testfabric.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(TagProvider::new);
        pack.addProvider(AdvancementProvider::new);
//        pack.addProvider(EnglishLanguageProvider::new);
//        pack.addProvider(ChineseLanguageProvider::new);
//        pack.addProvider(OreModelProvider::new);
        pack.addProvider(ModBlockLootTables::new);
        pack.addProvider(BlockTagProvider::new);
    }

    private static class ModBlockLootTables extends FabricBlockLootTableProvider {

        protected ModBlockLootTables(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            addDrop(ModBlocks.SILVER_ORE, ModItems.SILVER_ORE);
            addDrop(ModBlocks.TIN_ORE, ModItems.TIN_ORE);
            addDrop(ModBlocks.LITHIUM_ORE, ModItems.LITHIUM_ORE);
            addDrop(ModBlocks.TITANIUM_ORE, ModItems.TITANIUM_ORE);
            addDrop(ModBlocks.LEAD_ORE, ModItems.LEAD_ORE);
            addDrop(ModBlocks.ALUMINIUM_ORE, ModItems.ALUMINIUM_ORE);
            addDrop(ModBlocks.URANIUM_ORE, ModItems.URANIUM_ORE);
            addDrop(ModBlocks.PHOSPHORUS_ORE, ModItems.PHOSPHORUS_ORE);
            addDrop(ModBlocks.SULFUR_ORE, ModItems.SULFUR_ORE);
            addDrop(ModBlocks.RARE_EARTH_ORE, ModItems.RARE_EARTH_ORE);
            addDrop(ModBlocks.MINING_MACHINE_BLOCK);
            addDrop(ModBlocks.TRANSPORT_COMBINER_BLOCK);
            addDrop(ModBlocks.TRANSPORT_PIPE_BLOCK);
            addDrop(ModBlocks.DISPLAY_BLOCK);
            addDrop(ModBlocks.VERTICAL_HALF_BRICK_BLOCK);
            addDrop(ModBlocks.PIPE_FILTER_BLOCK);
            addDrop(ModBlocks.REDSTONE_TRANSFORM_ENGINE);
        }
    }

    private static class TagProvider extends FabricTagProvider<Item> {
        public static final TagKey<Item> INDUSTRIAL_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("testfabric", "industrial_blocks"));

        /**
         * Constructs a new {@link FabricTagProvider} with the default computed path.
         *
         * <p>Common implementations of this class are provided.
         *
         * @param output           the {@link FabricDataOutput} instance
         * @param registriesFuture the backing registry for the tag type
         */
        public TagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.ITEM, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(INDUSTRIAL_BLOCKS)
                    .add(ModItems.TRANSPORT_COMBINER)
                    .add(ModItems.DISPLAY_BLOCK)
                    .add(ModItems.MINING_MACHINE)
                    .add(ModItems.TRANSPORT_PIPE);
        }
    }

    private static class BlockTagProvider extends FabricTagProvider<Block> {
        public static final TagKey<Block> NEEDS_DIAMOND_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("testfabric", "needs_diamond_tool"));
        public static final TagKey<Block> NEEDS_IRON_TOOL = TagKey.of(RegistryKeys.BLOCK, new Identifier("testfabric", "needs_iron_tool"));
        public static final TagKey<Block> NEEDS_STONE_TOOL = TagKey.of(RegistryKeys.BLOCK, new Identifier("testfabric", "needs_stone_tool"));
//        public static final TagKey<Block> NEEDS_WOOD_TOOL = TagKey.of(RegistryKeys.BLOCK, new Identifier("testfabric", "needs_wood_tool"));
//        public static final TagKey<Block> NEED_LEVEL_0_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier("testfabric", "mineable/needs_tool_level_0"));

        /**
         * Constructs a new {@link FabricTagProvider} with the default computed path.
         *
         * <p>Common implementations of this class are provided.
         *
         * @param output           the {@link FabricDataOutput} instance
         * @param registriesFuture the backing registry for the tag type
         */
        public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.BLOCK, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(NEEDS_DIAMOND_BLOCK)
                    .add(ModBlocks.TITANIUM_ORE)
                    .add(ModBlocks.LEAD_ORE)
                    .add(ModBlocks.URANIUM_ORE)
                    .add(ModBlocks.RARE_EARTH_ORE);

            getOrCreateTagBuilder(NEEDS_IRON_TOOL)
                    .add(ModBlocks.LITHIUM_ORE)
                    .add(ModBlocks.ALUMINIUM_ORE)
                    .add(ModBlocks.PHOSPHORUS_ORE)
                    .add(ModBlocks.TIN_ORE)
                    .add(ModBlocks.SILVER_ORE)
                    .add(ModBlocks.SULFUR_ORE)
                    .add(ModBlocks.TRANSPORT_COMBINER_BLOCK)
                    .add(ModBlocks.DISPLAY_BLOCK)
                    .add(ModBlocks.MINING_MACHINE_BLOCK)
                    .add(ModBlocks.REDSTONE_TRANSFORM_ENGINE);

            getOrCreateTagBuilder(NEEDS_STONE_TOOL)
                    .add(ModBlocks.TRANSPORT_PIPE_BLOCK)
                    .add(ModBlocks.PIPE_FILTER_BLOCK)
                    .add(ModBlocks.VERTICAL_HALF_BRICK_BLOCK);

//            getOrCreateTagBuilder(NEED_LEVEL_1_BLOCK);
//            getOrCreateTagBuilder(NEED_LEVEL_0_BLOCK);
        }
    }

    private static class AdvancementProvider extends FabricAdvancementProvider {

        protected AdvancementProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> consumer) {
            new Advancements().accept(consumer);
        }

        static class Advancements implements Consumer<Consumer<Advancement>> {
            @Override
            public void accept(Consumer<Advancement> advancementConsumer) {
                Advancement.Builder.create().display(
                                ModItems.MINING_MACHINE,
                                Text.literal("工业时代！起步！"),
                                Text.literal("aaa"),
                                new Identifier(""),
                                AdvancementFrame.TASK,
                                true,
                                true,
                                false
                        )
                        .criterion("industrial_start", InventoryChangedCriterion.Conditions.items(ModItems.TRANSPORT_COMBINER))
                        .build(advancementConsumer, "testfabric" + "/root");
            }
        }
    }

    private static class EnglishLanguageProvider extends FabricLanguageProvider {

        protected EnglishLanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add(ModBlocks.SILVER_ORE, "Silver Ore");
            translationBuilder.add(ModBlocks.TIN_ORE, "Tin Ore");
            translationBuilder.add(ModBlocks.LITHIUM_ORE, "Lithium Ore");
            translationBuilder.add(ModBlocks.TITANIUM_ORE, "Titanium Ore");
            translationBuilder.add(ModBlocks.LEAD_ORE, "Lead Ore");
            translationBuilder.add(ModBlocks.ALUMINIUM_ORE, "Aluminium Ore");
            translationBuilder.add(ModBlocks.URANIUM_ORE, "Uranium Ore");
            translationBuilder.add(ModBlocks.PHOSPHORUS_ORE, "Phosphorus Ore");
            translationBuilder.add(ModBlocks.SULFUR_ORE, "Sulfur Ore");
            translationBuilder.add(ModBlocks.RARE_EARTH_ORE, "Rare Earth Ore");

            try {
                Path path = dataOutput.getModContainer().findPath("assets/testfabric/lang/en_us.json").get();
                translationBuilder.add(path);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add existing language file!", e);
            }
        }
    }

    private static class ChineseLanguageProvider extends FabricLanguageProvider {

        protected ChineseLanguageProvider(FabricDataOutput dataOutput) {
            super(dataOutput, "zh_cn");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add(ModBlocks.SILVER_ORE, "银矿");
            translationBuilder.add(ModBlocks.TIN_ORE, "锡矿");
            translationBuilder.add(ModBlocks.LITHIUM_ORE, "锂矿");
            translationBuilder.add(ModBlocks.TITANIUM_ORE, "钛矿");
            translationBuilder.add(ModBlocks.LEAD_ORE, "铅矿");
            translationBuilder.add(ModBlocks.ALUMINIUM_ORE, "铝矿");
            translationBuilder.add(ModBlocks.URANIUM_ORE, "铀矿");
            translationBuilder.add(ModBlocks.PHOSPHORUS_ORE, "磷矿");
            translationBuilder.add(ModBlocks.SULFUR_ORE, "硫矿");
            translationBuilder.add(ModBlocks.RARE_EARTH_ORE, "稀土矿");

            try {
                Path path = dataOutput.getModContainer().findPath("assets/testfabric/lang/zh_cn.json").get();
                translationBuilder.add(path);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add existing language file!", e);
            }
        }
    }

    private static class OreModelProvider extends FabricModelProvider {

        public OreModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SILVER_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TIN_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LITHIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LEAD_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ALUMINIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.URANIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PHOSPHORUS_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SULFUR_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RARE_EARTH_ORE);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(ModItems.SILVER_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.TIN_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.LITHIUM_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.TITANIUM_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.LEAD_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.ALUMINIUM_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.URANIUM_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.PHOSPHORUS_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.SULFUR_ORE, Models.GENERATED);
            itemModelGenerator.register(ModItems.RARE_EARTH_ORE, Models.GENERATED);
        }
    }
}
