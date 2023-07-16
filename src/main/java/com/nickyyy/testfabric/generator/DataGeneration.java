package com.nickyyy.testfabric.generator;

import com.nickyyy.testfabric.item.ModItemGroup;
import com.nickyyy.testfabric.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DataGeneration implements DataGeneratorEntrypoint {

    public static final TagKey<Item> INDUSTRIAL_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("testfabric", "industrial_blocks"));

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(TagProvider::new);
        pack.addProvider(AdvancementProvider::new);
    }

    private static class TagProvider extends FabricTagProvider<Item> {

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
}
