package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.PartyManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.util.ChatUtil;
import com.bytespacegames.requeue.util.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class RequeuePartyList extends CommandBase {
    Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "requeuepartylist";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/requeuepartylist";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) throws CommandException {
        String players = RequeueMod.instance.caresAboutClient() ? Minecraft.getMinecraft().thePlayer.getName() : "";
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

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}