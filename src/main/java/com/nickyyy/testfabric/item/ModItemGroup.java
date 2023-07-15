package com.nickyyy.testfabric.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final RegistryKey<ItemGroup> MOD_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("testfabric", "mod_item_tab"));
}
