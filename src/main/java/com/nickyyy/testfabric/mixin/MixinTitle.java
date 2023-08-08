package com.nickyyy.testfabric.mixin;

import com.nickyyy.testfabric.util.ModLog;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(TitleScreen.class)
public class MixinTitle {
    @Inject(method = "init()V", at = @At("HEAD"))
    private void InjectMethod(CallbackInfo ci) {
        ModLog.LOGGER.info("this is the mixin log");
    }
}
