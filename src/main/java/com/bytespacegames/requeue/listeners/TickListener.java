package com.bytespacegames.requeue.listeners;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.auto.TabRequeue;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.util.GameUtil;
import com.bytespacegames.requeue.util.Timer;
import com.bytespacegames.requeue.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.annotation.Annotation;

@SuppressWarnings("ALL")
public class TickListener implements Mod.EventHandler {
    private final Timer locrawTimer = new Timer();
    private final Timer kickofflineTimer = new Timer();
    private final Timer endRequeueTimer = new Timer();
    private boolean endRequeueTriggered = false;
    private boolean awaitingKickOffline = false;
    private boolean returnedLastTick = false;
    public void prepareKickOffline() {
        kickofflineTimer.reset();
        awaitingKickOffline=true;
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!RequeueMod.instance.modEnabled()) return;
        handleKickOffline();
        handleLocraw();
        handleAuto();
    }
    public void handleKickOffline() {
        if (awaitingKickOffline && kickofflineTimer.hasTimeElapsed(5000, true)) {
            awaitingKickOffline = false;
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p kickoffline");
        }
    }
    public void resetTimer() {
        locrawTimer.reset();
    }
    public void requeue() {
        String s = GameUtil.getGameID(LocationManager.instance.getType(), LocationManager.instance.getMode());
        if (s == null) {
            ChatUtil.displayMessageWithColor("There was an issue finding your game mode right now!");
            return;
        }
        ChatUtil.displayMessageWithColor("Attempted requeue.");
        RequeueMod.instance.getRequeue().requeueCleanup();
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/play " + s);
    }
    public void handleAuto() {
        if (LocationManager.instance == null) return;
        if (LocationManager.instance.getType() == null) return;
        if (LocationManager.instance.getMode() == null) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("PIT")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("SKYBLOCK")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("REPLAY")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("HOUSING")) return;

        boolean useTab = LocationManager.instance.getType().equals("DUELS") || LocationManager.instance.getType().equals("PROTOTYPE");
        if (useTab && !(RequeueMod.instance.getRequeue() instanceof TabRequeue)) {
            RequeueMod.instance.setRequeue(new TabRequeue());
        }
        if (!useTab && !(RequeueMod.instance.getRequeue() instanceof WhoRequeue)) {
            RequeueMod.instance.setRequeue(new WhoRequeue());
        }
        // handle requeueing prompted by a game end detected in ChatListener
        if (endRequeueTriggered && endRequeueTimer.hasTimeElapsed(500,false)) {
            endRequeueTriggered = false;
            requeue();
            return;
        }
        RequeueMod.instance.getRequeue().onTick();
    }
    public void handleLocraw() {
        if (!LocationManager.instance.isAwaitingLocraw()) { returnedLastTick = true; return; }
        if (Minecraft.getMinecraft().theWorld == null) { returnedLastTick = true;  return; }
        if (Minecraft.getMinecraft().thePlayer == null) { returnedLastTick = true; return; }
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain) {  returnedLastTick = true; return; }

        if (returnedLastTick) {
            locrawTimer.reset();
        }
        // check timer
        if (locrawTimer.hasTimeElapsed(5000,true)) {
            LocationManager.instance.sendLocraw();
        }
        returnedLastTick = false;
    }
    public void onGameEnd() {
        endRequeueTimer.reset();
    }
    public Class<? extends Annotation> annotationType() { return null; }
}
