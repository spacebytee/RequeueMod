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
import java.util.List;

public class WhoRequeue implements IAutoRequeue {
    final Timer whoTimer = new Timer();
    final Timer requeueTimer = new Timer();
    private List<String> lastTickNames = new ArrayList<>();
    private final List<String> whoNames = new ArrayList<>();
    private boolean isWhoValid() {
        // in blitz, when names are obfuscated, /who just displays the player count, but it is formatted
        // the same as a name, so we need to handle it to make sure it doesn't just requeue
        if (LocationManager.instance.getType().equalsIgnoreCase("SURVIVAL_GAMES")) {
            // if there is only one player in the who-list, and it is a two-digit number, don't requeue
            if (whoNames.size() == 1) {
                try {
                    int number = Integer.parseInt(whoNames.get(0));
                    if (number <= 99) {
                        return false;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return true;
    }
    public void addWhoName(String name) {
        if (name.isEmpty()) {
            return;
        }
        whoNames.add(name.trim());
    }
    public void clearWhoNames() {
        whoNames.clear();
    }
    private boolean playerLeft(List<String> from, List<String> to) {
        for (String s : from) {
            if (!to.contains(s)) {
                return true;
            }
        }
        return false;
    }
    boolean unhandledPlayerLeft = false;
    public void handleSendWho() {
        List<String> names = new ArrayList<>();
        for (NetworkPlayerInfo n : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            names.add(n.getGameProfile().getName().toLowerCase().trim());
        }
        unhandledPlayerLeft = playerLeft(lastTickNames,names);
        boolean canRecheckWho = whoNames.isEmpty() || playerLeft(lastTickNames,names);
        lastTickNames = names;

        if ((canRecheckWho || unhandledPlayerLeft)  && LocationManager.instance.getMode() != null && whoTimer.hasTimeElapsed(5000, true)) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/who");
            RequeueMod.instance.getChatHandler().criteria.clear();
            RequeueMod.instance.getChatHandler().criteria.add("ONLINE:");
            RequeueMod.instance.getChatHandler().criteria.add("ALIVE:");
            RequeueMod.instance.getChatHandler().criteria.add("This command is not available on this server!");
            RequeueMod.instance.getChatHandler().criteria.add("Couldn't find players, sorry!");
            RequeueMod.instance.getChatHandler().criteria.add("Command not supported!");
            RequeueMod.instance.getChatHandler().criteria.add("This command is not available while player names are scrambled!");
            unhandledPlayerLeft = false;
        }
    }
    public boolean canRequeue() {
        for (String s : whoNames) {
            if (PartyManager.instance.partyContains(s)) {
                return false;
            }
        }
        if (RequeueMod.instance.getSettingByName("clientplayer").isEnabled()) {
            return !whoNames.contains(Minecraft.getMinecraft().thePlayer.getName().trim());
        }
        return true;
    }
    public void requeueCleanup() {
        lastTickNames.clear();
        clearWhoNames();
    }
    @Override
    public void onTick() {
        if (!LocationManager.instance.isLocrawValid()) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain) return;
        if (LocationManager.instance.getType() == null) return;
        handleSendWho();
        if (whoNames.isEmpty()) return;
        if (!RequeueMod.instance.getSettingByName("auto").isEnabled()) return;
        if (isWhoValid() && canRequeue() && requeueTimer.hasTimeElapsed(10000,true)) {
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
}
