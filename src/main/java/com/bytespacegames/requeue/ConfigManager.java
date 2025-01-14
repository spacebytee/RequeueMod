package com.bytespacegames.requeue;


import com.bytespacegames.requeue.settings.Setting;
import com.sun.media.jfxmedia.logging.Logger;
import net.minecraft.client.Minecraft;

import java.io.*;

public class ConfigManager {

    public static void saveSettings() {
        File CONFIG_FILE = new File(Minecraft.getMinecraft().mcDataDir, "config/spacerequeue.config");
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                for (Setting setting : RequeueMod.instance.getSettings()) {
                    writer.write(setting.getName() + ":" + setting.representValue() + "\n");
                }
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static void loadSettings() {
        File CONFIG_FILE = new File(Minecraft.getMinecraft().mcDataDir, "config/spacerequeue.config");
        if (!CONFIG_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                Setting setting = RequeueMod.instance.getSettingByName(key);
                if (setting == null) {
                    Logger.logMsg(Logger.WARNING, "Configuration file is incorrect. Outdated?");
                    continue;
                }
                setting.parseValue(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}