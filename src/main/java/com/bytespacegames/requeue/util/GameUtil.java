package com.bytespacegames.requeue.util;

import com.bytespacegames.requeue.LocationManager;
import com.bytespacegames.requeue.RequeueMod;
import net.minecraft.client.Minecraft;

public class GameUtil {
    public static String getGameID(String type, String mode) {
        if (mode == null) return null;
        if ("normal".equals(mode) && !"GINGERBREAD".equals(type) && !"MCGO".equals(type))
            return type;

        //MURDER_MYSTERY, BEDWARS, DUELS all return mode. cases merged with default
        switch (type) {
            case "SKYBLOCK": return "skyblock";
            case "PIT": return "pit";
            case "TNTGAMES": return "tnt_" + mode;
            case "SPEED_UHC": return "speed_" + mode;
            case "UHC": return "uhc_" + mode;
            case "PROTOTYPE": return "prototype_" + mode;
            case "SURVIVAL_GAMES": return "blitz_" + mode;
            case "GINGERBREAD": return "tkr";
            case "QUAKECRAFT": return "quake_" + mode;
            case "ARENA": return "arena_" + mode;
            case "MCGO": return "mcgo_" + mode;
            case "WALLS3": return "mw_" + mode;
            case "BATTLEGROUND":  return "warlords_" + mode;
            case "SUPER_SMASH": return "super_smash_" + mode;
            case "WOOL_GAMES": return "wool_" + mode;
            case "ARCADE":
                switch (mode) {
                    case "ENDER": return "arcade_" + "ender_spleef";
                    case "DRAGONWARS2": return "arcade_" + "dragon_wars";
                    case "DRAW_THEIR_THING": return "arcade_" + "pixel_painters";
                    case "ONEINTHEQUIVER": return "arcade_" + "bounty_hunters";
                    case "DAYONE": return "arcade_" + "day_one";
                    case "PARTY": return "arcade_" + "party_games_1";
                    case "FARM_HUNT": return "arcade_" + mode;
                    case "DEFENDER": return "arcade_" + "creeper_defense";
                }
                return "arcade_" + mode; // Default fallback for arcade modes
            default:
                return mode;
        }
    }
    public static void safeRequeue() {
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
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/play " + s);
    }
}
