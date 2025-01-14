package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.ConfigManager;
import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.settings.BooleanSetting;
import com.bytespacegames.requeue.settings.Setting;
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
        Setting setting = RequeueMod.instance.getSettingByName(param);
        if (setting == null) {
            ChatUtil.displayMessageWithColor("That action couldn't be found, sorry! <auto/safeguard/kickoffline/clientplayer/requeueonwin/hypixelonly>");
            return;
        }
        if (!(setting instanceof BooleanSetting)) {
            ChatUtil.displayMessageWithColor("That setting cannot be toggled! <auto/safeguard/kickoffline/clientplayer/requeueonwin/hypixelonly>");
            return;
        }
        BooleanSetting set = (BooleanSetting) setting;
        set.toggle();
        ChatUtil.displayMessageWithColor("Toggled " + set.getName() + " to " + set.isEnabled() + ".");
        new Thread(ConfigManager::saveSettings).start();
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
        if ((!RequeueMod.instance.getRequeue().canRequeue()) && RequeueMod.instance.getSettingByName("safeguard").isEnabled()) {
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