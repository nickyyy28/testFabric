package com.nickyyy.testfabric.item;

import com.nickyyy.testfabric.block.ModBlocks;
import com.nickyyy.testfabric.weapons.SteelArmorMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ModItems {
    public static final Item STRAWBERRY = new Item(new FabricItemSettings().food(ModFoodComponents.STRAWBERRY).maxCount(64));
    public static final Item HORN = new HornItem(new Item.Settings().maxCount(1));
    public static final Item REDSTONE_TRANSFORM_ENGINE = new BlockItem(ModBlocks.REDSTONE_TRANSFORM_ENGINE, new FabricItemSettings().maxCount(64));
    public static final Item MINING_MACHINE = new BlockItem(ModBlocks.MINING_MACHINE_BLOCK, new FabricItemSettings().maxCount(64));
    public static final Item VERTICAL_HALF_BRICK = new BlockItem(ModBlocks.VERTICAL_HALF_BRICK_BLOCK, new FabricItemSettings().maxCount(64));
    public static final Item TRANSPORT_PIPE = new BlockItem(ModBlocks.TRANSPORT_PIPE_BLOCK, new FabricItemSettings().maxCount(64));
    public static final Item TRANSPORT_COMBINER = new BlockItem(ModBlocks.TRANSPORT_COMBINER_BLOCK, new FabricItemSettings().maxCount(64));
    public static final Item DISPLAY_BLOCK = new BlockItem(ModBlocks.DISPLAY_BLOCK, new FabricItemSettings().maxCount(64));

    public static final ArmorMaterial STEEL_ARMOR_MATERIAL = new SteelArmorMaterial();
    public static final Item STEEL_MATERIAL = new Item(new Item.Settings());
    public static final Item STEEL_HELMET = new ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1));
    public static final Item STEEL_CHESTPLATE = new ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxCount(1));
    public static final Item STEEL_LEGGINGS = new ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings().maxCount(1));
    public static final Item STEEL_BOOTS = new ArmorItem(STEEL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings().maxCount(1));
}
