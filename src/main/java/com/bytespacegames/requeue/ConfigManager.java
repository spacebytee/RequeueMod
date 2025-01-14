package com.bytespacegames.requeue;


import net.minecraft.client.Minecraft;

import java.io.*;

public class ConfigManager {

    public static void saveSettings() {
        File CONFIG_FILE = new File(Minecraft.getMinecraft().mcDataDir, "config/spacerequeue.config");
        try {
            // Create the directory if it doesn't exist
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }

            // Create or overwrite the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                RequeueMod mod = RequeueMod.instance;
                writer.write("isAuto:" + mod.isAuto() + "\n");
                writer.write("careAboutClient:" + mod.caresAboutClient() + "\n");
                writer.write("safeGuardManual:" + mod.safeguardManualRequeues() + "\n");
                writer.write("kickOffline:" + mod.kickOffline() + "\n");
                writer.write("requeueOnWin:" + mod.requeueOnWin() + "\n");
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace(); // Log the error
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
                boolean parsedValue = Boolean.parseBoolean(value);

                RequeueMod mod = RequeueMod.instance;
                switch (key) {
                    case "isAuto":
                        if (mod.isAuto() != parsedValue) {
                            mod.toggleAuto();
                        }
                        break;
                    case "careAboutClient":
                        if (mod.caresAboutClient() != parsedValue) {
                            mod.toggleClientPlayer();
                        }
                        break;
                    case "safeGuardManual":
                        if (mod.safeguardManualRequeues() != parsedValue) {
                            mod.toggleSafeGuardManual();
                        }
                        break;
                    case "kickOffline":
                        if (mod.kickOffline() != parsedValue) {
                            mod.toggleKickOffline();
                        }
                        break;
                    case "requeueOnWin":
                        if (mod.requeueOnWin() != parsedValue) {
                            mod.toggleRequeueOnWin();
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}