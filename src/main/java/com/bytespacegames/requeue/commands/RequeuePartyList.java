package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.PartyManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class RequeuePartyList extends CommandBase {
    Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "requeueparty";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/requeueparty";
    }

    public void list() {
        String players = RequeueMod.instance.getSettingByName("clientplayer").isEnabled() ? Minecraft.getMinecraft().thePlayer.getName() : "";
        // string builders are for nerds.
        for (String player : PartyManager.instance.getParty()) {
            players += ", " + player;
        }
        if (players.startsWith(", ")) {
            players = players.substring( 2);
        }
        if (players.endsWith(", ")) {
            players = players.substring(0,players.length() - 2);
        }
        players += ".";
        ChatUtil.displayMessageWithColor(players);
    }
    public void add(String player) {
        PartyManager.instance.registerPlayer(player);
        ChatUtil.displayMessageWithColor("Added " + player + " to the party list.");
    }
    public void remove(String player) {
        boolean worked = PartyManager.instance.removePlayer(player);
        if (worked) {
            ChatUtil.displayMessageWithColor("Removed " + player + " from the party list.");
        } else {
            ChatUtil.displayMessageWithColor("Couldn't find " + player + " in the party list.");
        }
    }
    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length == 0) {
            ChatUtil.displayMessageWithColor("Please specify what to do with the party. <add/remove/clear/list>");
            return;
        }
        if (args[0].equalsIgnoreCase("list")) {
            list();
            return;
        }
        if (args[0].equalsIgnoreCase("clear")) {
            PartyManager.instance.clearParty();
            return;
        }
        if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")) {
            ChatUtil.displayMessageWithColor("Please specify a valid action to do with the party. <add/remove/clear/list>");
            return;
        }
        if (args.length == 1) {
            ChatUtil.displayMessageWithColor("Adding or removing players requires you to specify the player to remove.");
            return;
        }
        if (args[0].equalsIgnoreCase("add")) {
            add(args[1]);
            return;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            remove(args[1]);
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}