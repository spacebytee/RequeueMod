package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.util.GameUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class Rq extends CommandBase {
    @Override
    public String getCommandName() {
        return "rq";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/rq";
    }
    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        GameUtil.safeRequeue();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}