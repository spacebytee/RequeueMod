package com.bytespacegames.requeue;

import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.listeners.ChatListener;
import net.minecraft.client.Minecraft;
import org.json.JSONObject;

public class LocationManager {
    public static LocationManager instance;
    private String locraw = null;
    private Minecraft mc;
    private ChatListener chatHandler;
    private boolean awaitingLocraw = true;
    public void invalidateLocraw() {
        locraw = null;
        awaitingLocraw = true;
        if (RequeueMod.instance.getRequeue() instanceof WhoRequeue) {
            ((WhoRequeue)RequeueMod.instance.getRequeue()).invalidateNames();
        }
    }
    public boolean isAwaitingLocraw() {
        return awaitingLocraw;
    }
    public void setLocraw(String message) {
        locraw = message;
    }
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
        return obj.getString("gametype").toString();
    }
    public String getMode() {
        if (locraw == null) return null;
        JSONObject obj = new JSONObject(locraw);
        if (!obj.has("mode")) return null;
        return obj.getString("mode").toString();
    }
}
