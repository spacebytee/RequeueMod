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
            ChatUtil.displayMessageWithColor("That action couldn't be found, sorry! For more info, use /requeue help.");
            return;
        }
        if (!(setting instanceof BooleanSetting)) {
            ChatUtil.displayMessageWithColor("That setting cannot be toggled! For more info, use /requeue help.");
            return;
        }
        BooleanSetting set = (BooleanSetting) setting;
        set.toggle();
        ChatUtil.displayMessageWithColor("Toggled " + set.getName() + " to " + set.isEnabled() + ".");
        new Thread(ConfigManager::saveSettings).start();
    }
    public void help() {
        String built = "Settings:\n";
        for (Setting s : RequeueMod.instance.getSettings()) {
            built += RequeueMod.PRIMARY_COLOR +  "/requeue " + s.getName() + RequeueMod.TEXT_COLOR + " - " + s.getDescription() + "\n";
        }
        built += RequeueMod.TEXT_COLOR + "You can also use /requeueparty to manage your party.";
        ChatUtil.displayMessageWithColor(built);
    }
    @Override
    public void processCommand(ICommandSender iCommandSender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                help();
                return;
            }
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
        RequeueMod.instance.getRequeueTimer().reset();
        ChatUtil.displayMessageWithColor("Attempted requeue.");
        RequeueMod.instance.getRequeue().requeueCleanup();
        mc.thePlayer.sendChatMessage("/play " + s);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}