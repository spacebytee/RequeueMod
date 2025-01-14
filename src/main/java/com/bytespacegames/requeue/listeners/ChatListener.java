package com.bytespacegames.requeue.listeners;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.PartyManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassExplicitlyAnnotation")
public class ChatListener implements Mod.EventHandler {
    public final List<String> criteria = new ArrayList<>();
    public long waitingSince = Long.MAX_VALUE;
    public void timeCriteria() {
        waitingSince = System.currentTimeMillis();
    }
    public void listenForJoins(String noColors) {
        if (noColors.contains(":")) return;
        if (noColors.startsWith("You left the party.")) {
            PartyManager.instance.clearParty();
            return;
        }
        if (noColors.startsWith("You have joined")) {
            PartyManager.instance.clearParty();
            String player = noColors.split(" ")[3];
            if (player.contains("[")) player = noColors.split(" ")[4];
            player = player.substring(0,player.length()-2);
            PartyManager.instance.registerPlayer(player);
            return;
        }
        if (noColors.contains("has left the party")) {
            String player = noColors.split(" ")[0];
            if (player.contains("[")) player = noColors.split(" ")[1];
            PartyManager.instance.removePlayer(player);
            return;
        }
        if (noColors.contains("has been removed from the party.")) {
            String player = noColors.split(" ")[0];
            if (player.contains("[")) player = noColors.split(" ")[1];
            PartyManager.instance.removePlayer(player);
            return;
        }
        if (noColors.contains("joined the party")) {
            String player = noColors.split(" ")[0];
            if (player.contains("[")) player = noColors.split(" ")[1];
            PartyManager.instance.removePlayer(player);
            return;
        }
        if (noColors.startsWith("Kicked") && noColors.endsWith("because they were offline.")) {
            String player = noColors.split(" ")[1];
            if (player.contains("[")) player = noColors.split(" ")[2];
            PartyManager.instance.registerPlayer(player);
            return;
        }
        // why is this line here????
        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;

        if (noColors.contains("has disconnected") && RequeueMod.instance.getSettingByName("kickoffline").isEnabled()) {
            RequeueMod.instance.getTickListener().prepareKickOffline();
        }
    }
    public void listenForParty(String msg) {
        String noColors = ChatUtil.removeColorCodes(msg).trim();
        listenForJoins(noColors);
        // detect for
        if (!noColors.contains(":")) return;
        if (!noColors.startsWith("You'll be partying")) return;
        String secondHalf = noColors.split(":",2)[1];
        String[] args = secondHalf.split(",\\s*|\\s+");
        for (String s : args) {
            if (s.contains("[")) continue;
            PartyManager.instance.registerPlayer(s);
        }
    }
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (!RequeueMod.instance.modEnabled()) return;
        String message = e.message.getUnformattedText();
        String removedColors = ChatUtil.removeColorCodes(message).trim();

        // handle game ending
        if (((removedColors.contains("Reward Summary") || (removedColors.contains("WINNER!")) && !removedColors.contains(":"))) && RequeueMod.instance.getSettingByName("requeueonwin").isEnabled()) {
            RequeueMod.instance.getTickListener().onGameEnd();
        }
        listenForParty(message);
        // handle locraw responses
        if (message.startsWith("{\"server\":")) {
            LocationManager.instance.setLocraw(message);
        }
        // handle WHO parsing
        if (removedColors.startsWith("ONLINE:") && RequeueMod.instance.getRequeue() instanceof WhoRequeue) {
            String[] playerList = removedColors.split(":",2)[1].split(",");
            ((WhoRequeue)RequeueMod.instance.getRequeue()).clearWhoNames();
            for (String p : playerList) {
                ((WhoRequeue)RequeueMod.instance.getRequeue()).addWhoName(p.trim());
            }
        }
        // handle hiding the message if prepared for
        if (criteria.isEmpty()) return;
        boolean blocked = false;
        for (String s : criteria) {
            if (removedColors.contains(s)) {
                e.setCanceled(true);
                blocked = true;
                break;
            }
        }
        if (blocked) {
            criteria.clear();
        }
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (System.currentTimeMillis() - waitingSince > 5000) {
            criteria.clear();
            waitingSince = Long.MAX_VALUE;
        }
    }

    public Class<? extends Annotation> annotationType() { return null; }
}
