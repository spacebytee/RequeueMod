package com.bytespacegames.requeue.listeners;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.annotation.Annotation;

public class TickListener implements Mod.EventHandler {
    long timeSinceInLobby = Long.MAX_VALUE;
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        handleLocraw();
        handleAuto();
    }
    public void resetTimer() {
        timeSinceInLobby = Long.MAX_VALUE;
    }
    public void handleAuto() {
        RequeueMod.instance.getRequeue().onTick();
    }
    public void handleLocraw() {
        if (!LocationManager.instance.isAwaitingLocraw()) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain) return;
        // set timer
        long epoch = System.currentTimeMillis();
        if (epoch < timeSinceInLobby) timeSinceInLobby = epoch;
        // check timer
        if (epoch - timeSinceInLobby > 5000) {
            LocationManager.instance.sendLocraw();
            timeSinceInLobby = Long.MAX_VALUE;
        }
    }
    public Class<? extends Annotation> annotationType() { return null; }
}
