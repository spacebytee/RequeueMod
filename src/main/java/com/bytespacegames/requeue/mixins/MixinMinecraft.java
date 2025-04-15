package com.bytespacegames.requeue.mixins;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.auto.IAutoRequeue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method="loadWorld", at=@At("TAIL"))
    public void mixin$loadWorld(WorldClient p_71403_1_, CallbackInfo ci) {
        if (!RequeueMod.instance.modEnabled()) return;
        if (RequeueMod.instance.getSettingByName("useforge").isEnabled()) return;
        LocationManager.instance.invalidateLocraw();
        RequeueMod.instance.getTickListener().resetTimer();
        IAutoRequeue r = RequeueMod.instance.getRequeue();
        r.requeueCleanup();
    }
}
