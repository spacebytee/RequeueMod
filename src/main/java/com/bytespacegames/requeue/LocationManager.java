package com.bytespacegames.requeue;

import com.bytespacegames.requeue.listeners.ChatListener;
import net.minecraft.client.Minecraft;
import org.json.JSONObject;

public class LocationManager {
    public static LocationManager instance;
    private String locraw = null;
    private final Minecraft mc;
    private final ChatListener chatHandler;
    private boolean awaitingLocraw = true;
    public void invalidateLocraw() {
        locraw = null;
        awaitingLocraw = true;
        RequeueMod.instance.getRequeue().requeueCleanup();
    }
    public boolean isAwaitingLocraw() {
        return awaitingLocraw;
    }
    public void setLocraw(String message) {
        locraw = message;
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLocrawValid() {
        return locraw != null;
    }
    public void sendLocraw() {
        mc.thePlayer.sendChatMessage("/locraw");
        awaitingLocraw = false;
        chatHandler.criteria.clear();
        chatHandler.criteria.add("{\"server\":");
    }
    public LocationManager() {
        mc = Minecraft.getMinecraft();
        chatHandler = RequeueMod.instance.getChatHandler();
        instance = this;
    }
    public String getType() {
        if (locraw == null) return null;
        JSONObject obj = new JSONObject(locraw);
        if (!obj.has("gametype")) return null;
        return obj.getString("gametype").trim();
    }
    public String getMode() {
        if (locraw == null) return null;
        JSONObject obj = new JSONObject(locraw);
        if (!obj.has("mode")) return null;
        return obj.getString("mode");
    }

    public String getLocraw() {
        return locraw;
    }
}