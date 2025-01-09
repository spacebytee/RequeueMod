package com.bytespacegames.requeue.listeners;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
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
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (criteria.isEmpty()) return;
        String message = e.message.getUnformattedText();
        for (String s : criteria) {
            if (message.contains(s)) {
                e.setCanceled(true);
                break;
            }
        }
        if (message.startsWith("{\"server\":")) {
            LocationManager.instance.setLocraw(message);
        }
        String removedColors = ChatUtil.removeColorCodes(message);
        if (message.startsWith("ONLINE:")) {
            //TODO: parse
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
