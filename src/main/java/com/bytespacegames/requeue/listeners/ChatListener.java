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
    private long waitingSince = Long.MAX_VALUE;
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
            PartyManager.instance.registerPlayer(player);
            return;
        }
        if (noColors.startsWith("Kicked") && noColors.endsWith("because they were offline.")) {
            String player = noColors.split(" ")[1];
            if (player.contains("[")) player = noColors.split(" ")[2];
            PartyManager.instance.removePlayer(player);
            return;
        }

        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
        if (noColors.contains("has disconnected") && RequeueMod.instance.getSettingByName("kickoffline").isEnabled()) {
            RequeueMod.instance.getTickListener().prepareKickOffline();
        }
    }
    public void listenForParty(String msg) {
        String noColors = ChatUtil.removeColorCodes(msg).trim();
        listenForJoins(noColors);
        // detect
        if (!noColors.contains(":")) return;
        if (!noColors.startsWith("You'll be partying")) return;
        String secondHalf = noColors.split(":",2)[1];
        String[] args = secondHalf.split(",\\s*|\\s+");
        for (String s : args) {
            if (s.contains("[")) continue;
            PartyManager.instance.registerPlayer(s);
        }
    }
    public void parseAsWho(String message) {
        String[] playerList = message.split(":",2)[1].split(",");
        ((WhoRequeue)RequeueMod.instance.getRequeue()).clearWhoNames();
        for (String p : playerList) {
            ((WhoRequeue)RequeueMod.instance.getRequeue()).addWhoName(p.trim());
        }
    }
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (!RequeueMod.instance.modEnabled()) return;
        String message = e.message.getUnformattedText();
        if (message.contains(RequeueMod.PRIMARY_COLOR + RequeueMod.MOD_PREFIX)) {
            return;
        }
        String removedColors = ChatUtil.removeColorCodes(message).trim();

        // handle game ending
        if (((removedColors.contains("Reward Summary") || removedColors.contains("WINNER!")) && !removedColors.contains(":")) && RequeueMod.instance.getSettingByName("requeueonwin").isEnabled()) {
            RequeueMod.instance.getTickListener().onGameEnd();
        }
        listenForParty(message);
        // handle locraw responses
        if (message.startsWith("{\"server\":")) {
            LocationManager.instance.setLocraw(message);
        }
        // standard WHO parsing
        if (RequeueMod.instance.isUsingWhoRequeue() && removedColors.startsWith("ONLINE:") || removedColors.startsWith("ALIVE:")) {
            parseAsWho(removedColors);
        }
        // hide messages if in criteria
        hideCriteria(removedColors,e);
        // some games have annoying asf /who behavior, make sure they're handled after criteria is cleared
        if (RequeueMod.instance.isUsingWhoRequeue() && LocationManager.instance.getType() != null) {
            switch (LocationManager.instance.getType().toUpperCase()) {
                case "SKYWARS":
                    handleSkywars(removedColors);
                    break;
                case "WALLS":
                    handleWalls(removedColors);
                    break;
                case "MCGO":
                    handleCVC(removedColors);
                    break;
            }
        }
    }
    public void hideCriteria(String message, ClientChatReceivedEvent e) {
        if (criteria.isEmpty()) return;
        boolean blocked = false;
        for (String s : criteria) {
            if (message.contains(s)) {
                e.setCanceled(true);
                blocked = true;
                break;
            }
        }
        if (blocked) {
            criteria.clear();
            waitingSince = Long.MAX_VALUE;

            if (message.startsWith("ALIVE:")) {
                criteria.add("DEAD:");
            }
        }
    }
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (RequeueMod.instance.isUsingWhoRequeue()) {
            if (LocationManager.instance.getType() != null && !LocationManager.instance.getType().equalsIgnoreCase("SKYWARS"))
                ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(false);
        }

        // clear the criteria after 5 seconds of it having items
        if (!criteria.isEmpty() && waitingSince == Long.MAX_VALUE) {
            waitingSince = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - waitingSince > 5000) {
            criteria.clear();
            waitingSince = Long.MAX_VALUE;
        }
    }

    public void handleSkywars(String message) {
        if (message.startsWith("Mode:")) {
            ((WhoRequeue)RequeueMod.instance.getRequeue()).clearWhoNames();
            criteria.add("Team #");
            ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(false);
            return;
        }
        if (!message.startsWith("Team #")) {
            criteria.clear();
            ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(true);
            return;
        }
        String playerName = message.split(" ")[2];
        ((WhoRequeue)RequeueMod.instance.getRequeue()).addWhoName(playerName);
        criteria.add("Team #");
    }
    public void handleWalls(String message) {
        if (message.startsWith("Players Alive")) {
            ((WhoRequeue)RequeueMod.instance.getRequeue()).clearWhoNames();
            criteria.add("RED: ");
            ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(false);
            return;
        }
        for (String color : wallsColors) {
            if (!message.startsWith(color + ": ")) {
                continue;
            }
            if (!color.equals("YELLOW")) {
                criteria.add(getNextColor(color) + ": ");
            }
            String[] playerNames = message.split(": ", 2)[2].trim().split(", ");
            for (String playerName : playerNames) {
                ((WhoRequeue)RequeueMod.instance.getRequeue()).addWhoName(playerName.trim());
            }
            break;
        }
        if (message.startsWith("YELLOW: ")) {
            ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(true);
        }
    }
    public void handleCVC(String message) {
        if (message.startsWith("Crims: ") || message.startsWith("Cops: ") ) {
            if (message.startsWith("Cops: ")) {
                ((WhoRequeue)RequeueMod.instance.getRequeue()).clearWhoNames();
                criteria.add("Crims: ");
                ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(false);
                return;
            }
            String[] playerNames = message.split(": ", 2)[2].trim().split(", ");
            for (String playerName : playerNames) {
                ((WhoRequeue)RequeueMod.instance.getRequeue()).addWhoName(playerName.trim());
            }
        }
        if (message.startsWith("Crims: ")) {
            ((WhoRequeue)RequeueMod.instance.getRequeue()).setDelayedValid(true);
        }
    }
    private final String[] wallsColors = {"RED", "BLUE", "GREEN", "YELLOW"};
    public String getNextColor(String color) {
        switch (color) {
            case "RED": return "BLUE";
            case "BLUE": return "GREEN";
            case "GREEN": return "YELLOW";
            default: return "RED";
        }
    }

    public Class<? extends Annotation> annotationType() { return null; }
}
