package com.bytespacegames.requeue;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartyManager {
    public static PartyManager instance;
    private final List<String> party = new ArrayList<>();
    public PartyManager() {
        instance = this;
    }
    public void clearParty() {
        party.clear();
    }
    public void registerPlayer(String player) {
        if (player.isEmpty()) {
            return;
        }
        if (partyContains(player)) return;
        player = player.trim();
        party.add(player);
    }
    public boolean removePlayer(String player) {
        player = player.trim();
        for (Iterator<String> iterator = party.iterator(); iterator.hasNext(); ) {
            String currentPlayer = iterator.next();
            if (currentPlayer.equalsIgnoreCase(player)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean partyContains(String player) {
        return party.contains(player);
    }
    public List<String> getParty() {
        return party;
    }
}