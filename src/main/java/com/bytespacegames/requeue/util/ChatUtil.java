package com.bytespacegames.requeue.util;

import com.bytespacegames.requeue.RequeueMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {
    private static final List<String> messages = new ArrayList<>();
    public static void displayMessageWithColor(String message) {
        messages.add(message);
        if (Minecraft.getMinecraft().thePlayer == null) return;
        for (String msg : messages) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(RequeueMod.PRIMARY_COLOR + RequeueMod.MOD_PREFIX + " " + RequeueMod.TEXT_COLOR + "> " + msg));
        }
        messages.clear();
    }
    public static String removeColorCodes(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }
}
