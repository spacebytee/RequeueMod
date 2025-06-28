package com.bytespacegames.requeue.commands;

import com.bytespacegames.requeue.ConfigManager;
import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.PartyManager;
import com.bytespacegames.requeue.RequeueMod;
import com.bytespacegames.requeue.settings.BooleanSetting;
import com.bytespacegames.requeue.settings.Setting;
import com.bytespacegames.requeue.util.ChatUtil;
import com.bytespacegames.requeue.util.GameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.List;

public class Requeue extends CommandBase {
    final Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "requeue";
    }
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, RequeueMod.instance.settingsToArray());
        }
        return Collections.emptyList();
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
        GameUtil.safeRequeue();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}