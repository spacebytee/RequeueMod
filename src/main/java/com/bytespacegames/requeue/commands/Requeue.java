package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.ConfigManager;
import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.util.ChatUtil;
import com.bytespacegames.requeue.util.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class Requeue extends CommandBase {
    final Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "requeue";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/requeue";
    }
    public void handleToggle(String param) {
        boolean state;
        switch (param.toLowerCase()) {
            case "auto":
                state = RequeueMod.instance.toggleAuto();
                ChatUtil.displayMessageWithColor("Toggled auto to " + state + ".");
                break;
            case "safeguard":
                state = RequeueMod.instance.toggleSafeGuardManual();
                ChatUtil.displayMessageWithColor("Toggled safeguarding manual to " + state + ".");
                break;
            case "kickoffline":
                state = RequeueMod.instance.toggleKickOffline();
                ChatUtil.displayMessageWithColor("Toggled kick offline to " + state + ".");
                break;
            case "clientplayer":
                state = RequeueMod.instance.toggleClientPlayer();
                ChatUtil.displayMessageWithColor("Toggled considering the client player to " + state + ".");
                break;
            case "requeueonwin":
                state = RequeueMod.instance.toggleRequeueOnWin();
                ChatUtil.displayMessageWithColor("Toggled requeueing on win to " + state + ".");
                break;
            default:
                ChatUtil.displayMessageWithColor("That action couldn't be found, sorry! <auto/safeguard/kickoffline/clientplayer/requeueonwin>");
                break;
        }
        ConfigManager.saveSettings();
    }
    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length > 0) {
            handleToggle(args[0]);
            return;
        }
        String s = GameUtil.getGameID(LocationManager.instance.getType(), LocationManager.instance.getMode());
        if (s == null) {
            ChatUtil.displayMessageWithColor("There was an issue finding your game mode right now!");
            return;
        }
        if ((!RequeueMod.instance.getRequeue().canRequeue()) && RequeueMod.instance.safeguardManualRequeues()) {
            ChatUtil.displayMessageWithColor("You can't requeue now! Still people in game.");
            return;
        }
        ChatUtil.displayMessageWithColor("Attempted requeue.");
        RequeueMod.instance.getRequeue().requeueCleanup();
        mc.thePlayer.sendChatMessage("/play " + s);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}