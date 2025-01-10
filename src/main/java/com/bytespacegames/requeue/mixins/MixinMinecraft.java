package com.bytespacegames.requeue.mixins;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.auto.IAutoRequeue;
import com.bytespacegames.requeue.auto.WhoRequeue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "startGame", at = @At("HEAD"))
    private void startGame(CallbackInfo ci) {
        System.out.println("Mixin worked!");
    }
    @Inject(method="loadWorld", at=@At("TAIL"))
    public void mixin$loadWorld(WorldClient p_71403_1_, CallbackInfo ci) {
        LocationManager.instance.invalidateLocraw();
        RequeueMod.instance.getTickListener().resetTimer();
        IAutoRequeue r = RequeueMod.instance.getRequeue();
        r.requeueCleanup();
    }
}
