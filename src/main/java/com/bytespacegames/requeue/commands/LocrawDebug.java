package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

@SuppressWarnings("CommentedOutCode")
public class LocrawDebug extends CommandBase {
    Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "locrawdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/locrawdebug";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        ChatUtil.displayMessageWithColor("Recent Locraw: " + LocationManager.instance.getLocraw());
        ChatUtil.displayMessageWithColor("Awaiting Locraw to Send: " + LocationManager.instance.isAwaitingLocraw());
        //ChatUtil.displayMessageWithColor("Returned Last Tick (?): " + RequeueMod.instance.getTickListener().returnedLastTick);
        //ChatUtil.displayMessageWithColor("Last Return ID: " + RequeueMod.instance.getTickListener().returnId);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}