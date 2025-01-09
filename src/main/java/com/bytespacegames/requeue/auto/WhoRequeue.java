package com.bytespacegames.requeue.auto;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class WhoRequeue implements IAutoRequeue {
    com.bytespacegames.requeue.Utils.Timer whoTimer = new com.bytespacegames.requeue.Utils.Timer();
    private List<String> lastTickNames = new ArrayList<String>();
    private List<String> whoNames = new ArrayList<String>();
    public void invalidateNames() {
        lastTickNames.clear();
        whoNames.clear();
    }
    private boolean playerLeft(List<String> from, List<String> to) {
        for (String s : from) {
            if (!to.contains(from)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onTick() {
        if (!LocationManager.instance.isLocrawValid()) return;
        List<String> names = new ArrayList<String>();
        for (NetworkPlayerInfo n : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            names.add(n.getGameProfile().getName().toLowerCase());
        }
        boolean canRecheckWho = whoNames.isEmpty() || playerLeft(lastTickNames,names);
        lastTickNames = names;

        if (canRecheckWho && whoTimer.hasTimeElapsed(5000, true)) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/who");
            RequeueMod.instance.getChatHandler().criteria.clear();
            RequeueMod.instance.getChatHandler().criteria.add("ONLINE:");
        }
    }
}
