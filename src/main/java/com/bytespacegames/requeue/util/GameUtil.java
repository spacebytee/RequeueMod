package com.bytespacegames.requeue.util;

public class GameUtil {
    public static String getGameID(String type, String mode) {
        if (mode == null) return null;
        if ("normal".equals(mode) && !"GINGERBREAD".equals(type) && !"MCGO".equals(type))
            return type;

        switch (type) {
            case "MURDER_MYSTERY": return mode;
            case "SKYBLOCK": return "skyblock";
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
                if ("DEFENDER".equals(mode)) {
                    return "arcade_creeper_defense";
                } else if ("FARM_HUNT".equals(mode)) {
                    return "arcade_" + mode;
                } else if ("PARTY".equals(mode)) {
                    return "arcade_party_games_1";
                }
                if ("ENDER".equals(mode)) return "arcade_" + "ender_spleef";
                if ("DRAGONWARS2".equals(mode)) return "arcade_" + "dragon_wars";
                if ("DRAW_THEIR_THING".equals(mode)) return "arcade_" + "pixel_painters";
                if ("ONEINTHEQUIVER".equals(mode)) return "arcade_" + "bounty_hunters";
                if ("DAYONE".equals(mode)) return "arcade_" + "day_one";
                if ("PARTY".equals(mode)) return "arcade_" + "party_games_1";
                if ("DEFENDER".equals(mode)) return "arcade_" + "creeper_defense";
                return "arcade_" + mode; // Default fallback for arcade modes
            default:
                return null;
        }
    }
}
