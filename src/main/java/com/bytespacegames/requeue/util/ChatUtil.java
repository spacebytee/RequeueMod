package com.bytespacegames.requeue.util;

import com.bytespacegames.requeue.RequeueMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtil {
    public static void displayMessageWithColor(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(RequeueMod.MOD_PREFIX + " §e> " + message));
    }
    public static String removeColorCodes(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }
}
