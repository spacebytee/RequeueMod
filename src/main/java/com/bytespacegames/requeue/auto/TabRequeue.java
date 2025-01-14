package com.bytespacegames.requeue.auto;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.PartyManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.util.ChatUtil;
import com.bytespacegames.requeue.util.GameUtil;
import com.bytespacegames.requeue.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TabRequeue implements IAutoRequeue{
    final Timer requeueTimer = new Timer();
    @Override
    public void onTick() {
        if (!LocationManager.instance.isLocrawValid()) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain) return;
        if (LocationManager.instance.getType() == null) return;
        if (!RequeueMod.instance.getSettingByName("auto").isEnabled()) return;
        if (canRequeue() && requeueTimer.hasTimeElapsed(10000,true)) {
            String s = GameUtil.getGameID(LocationManager.instance.getType(), LocationManager.instance.getMode());
            if (s == null) {
                ChatUtil.displayMessageWithColor("There was an issue finding your game mode right now!");
                return;
            }
            ChatUtil.displayMessageWithColor("Attempted requeue.");
            requeueCleanup();
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/play " + s);
        }
    }

    @Override
    public boolean canRequeue() {
        List<String> dead = new ArrayList<>();
        for (NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()){
            try{
                if ((networkPlayerInfo.getPlayerTeam().getColorPrefix().contains("§7"))){
                    dead.add(networkPlayerInfo.getGameProfile().getName());
                }
            }
            catch(Exception ignored){}
        }
        return new HashSet<>(dead).containsAll(PartyManager.instance.getParty());
    }

    @Override
    public void requeueCleanup() {

    }
}
