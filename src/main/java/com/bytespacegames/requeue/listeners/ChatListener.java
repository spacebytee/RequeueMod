package com.bytespacegames.requeue.listeners;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.PartyManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.util.ChatUtil;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ChatListener implements Mod.EventHandler {
    public List<String> criteria = new ArrayList<String>();
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
            String player = noColors.split(" ")[3];
            if (player.contains("[")) player = noColors.split(" ")[4];
            PartyManager.instance.removePlayer(player);
            return;
        }
        if (noColors.contains("has joined the party")) {
            String player = noColors.split(" ")[3];
            if (player.contains("[")) player = noColors.split(" ")[4];
            PartyManager.instance.registerPlayer(player);
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
        listenForParty(e.message.getUnformattedText());

        String message = e.message.getUnformattedText();
        if (message.startsWith("{\"server\":")) {
            LocationManager.instance.setLocraw(message);
        }
        String removedColors = ChatUtil.removeColorCodes(message).trim();
        if (removedColors.startsWith("ONLINE:") && RequeueMod.instance.getRequeue() instanceof WhoRequeue) {
            String[] playerList = removedColors.split(":",2)[1].split(",");
            ((WhoRequeue)RequeueMod.instance.getRequeue()).clearWhoNames();
            for (String p : playerList) {
                ((WhoRequeue)RequeueMod.instance.getRequeue()).addWhoName(p.trim());
            }
        }

        if (criteria.isEmpty()) return;
        boolean blocked = false;
        for (String s : criteria) {
            if (removedColors.contains(s)) {
                //e.setCanceled(true);
                ChatUtil.displayMessageWithColor("would've hidden message but did not");
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
