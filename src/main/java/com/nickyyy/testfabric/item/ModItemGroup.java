package com.nickyyy.testfabric.item;

import com.nickyyy.testfabric.TestFabric;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

public class ModItemGroup {
    public static final RegistryKey<ItemGroup> MOD_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("testfabric", "mod_item_tab"));
}
