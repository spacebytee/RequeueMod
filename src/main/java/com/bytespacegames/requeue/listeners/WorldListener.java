package com.bytespacegames.requeue.listeners;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.auto.IAutoRequeue;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.annotation.Annotation;

@SuppressWarnings("ALL")
public class WorldListener implements Mod.EventHandler {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        if (!RequeueMod.instance.modEnabled()) return;
        if (!RequeueMod.instance.getSettingByName("useforge").isEnabled()) return;
        LocationManager.instance.invalidateLocraw();
        RequeueMod.instance.getTickListener().resetTimer();
        IAutoRequeue r = RequeueMod.instance.getRequeue();
        r.requeueCleanup();
    }
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
