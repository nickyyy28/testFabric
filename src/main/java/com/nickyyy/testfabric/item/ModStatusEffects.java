package com.nickyyy.testfabric.item;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;


public class ModStatusEffects {
//    public static final StatusEffect HAPPINESS = ModStatusEffects.register(101, "happiness", new StatusEffect(StatusEffectCategory.BENEFICIAL, 0xdb184f));

    private static StatusEffect register(int rawId, String id, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, rawId, id, entry);
    }
}
