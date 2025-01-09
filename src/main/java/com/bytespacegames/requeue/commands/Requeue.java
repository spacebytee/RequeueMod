package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.util.ChatUtil;
import com.bytespacegames.requeue.util.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class Requeue extends CommandBase {
    Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "requeue";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/requeue";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) throws CommandException {
        String s = GameUtil.getGameID(LocationManager.instance.getType(), LocationManager.instance.getMode());
        if (s == null) {
            ChatUtil.displayMessageWithColor("There was an issue finding your game mode right now!");
            return;
        }
        ChatUtil.displayMessageWithColor("Attempted requeue.");
        mc.thePlayer.sendChatMessage("/play " + s);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}