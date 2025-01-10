package com.bytespacegames.requeue;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {
    public static PartyManager instance;
    private List<String> party = new ArrayList<String>();
    public PartyManager() {
        instance = this;
    }
    public void clearParty() {
        party.clear();
        //tryRegisterClientPlayer();
    }
    public void tryRegisterClientPlayer() {
        String pName = Minecraft.getMinecraft().thePlayer.getName().trim();
        registerPlayer(pName);
    }
    public void registerPlayer(String player) {
        if (player.isEmpty()) {
            return;
        }
        player = player.trim();
        party.add(player);
    }
    public void removePlayer(String player) {
        player = player.trim();
        party.remove(player);
    }
    public boolean partyContains(String player) {
        return party.contains(player);
    }
    public List<String> getParty() {
        return party;
    }
}
