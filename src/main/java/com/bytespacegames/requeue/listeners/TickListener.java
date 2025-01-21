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
    private final Timer endTriggerTimer = new Timer();
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
        // under these conditions, there should be no auto of any kind, the regular methods nor chat detection
        if (LocationManager.instance == null) return;
        if (LocationManager.instance.getType() == null) return;
        if (LocationManager.instance.getMode() == null) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("PIT")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("SKYBLOCK")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("REPLAY")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("HOUSING")) return;
        handleWinRequeue();
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
    public void handleWinRequeue() {
        // handle requeueing prompted by a game end detected in ChatListener
        if (endRequeueTriggered && endRequeueTimer.hasTimeElapsed(500,false)) {
            endRequeueTriggered = false;
            requeue();
            return;
        }
    }
    public void handleAuto() {
        if (LocationManager.instance.getType().equals("DUELS")) return;
        if (LocationManager.instance.getType().equals("ARCADE") && LocationManager.instance.getMode().equals("PARTY")) return;
        if (LocationManager.instance.getType().equalsIgnoreCase("SKYWARS") && (LocationManager.instance.getMode().contains("teams") || LocationManager.instance.getMode().contains("mega"))) return;
        // set the correct requeue method
        boolean useTab = LocationManager.instance.getType().equals("PROTOTYPE");
        if (useTab && !(RequeueMod.instance.getRequeue() instanceof TabRequeue)) {
            RequeueMod.instance.setRequeue(new TabRequeue());
        }
        if (!useTab && !(RequeueMod.instance.isUsingWhoRequeue())) {
            RequeueMod.instance.setRequeue(new WhoRequeue());
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
        // only trigger a game end after a delay of 5s, because sometimes seperate messages will trigger the same game to requeue twice.
        // endTriggerTimer is solely for preventing this, endRequeueTimer is for having a slight delay after a requeue is triggered
        if (endTriggerTimer.hasTimeElapsed(5000, true)) {
            endRequeueTriggered = true;
            endRequeueTimer.reset();
        }
    }
    public Class<? extends Annotation> annotationType() { return null; }
}
