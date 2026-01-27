package de.myownbrain.autoLogout.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.InputConstants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/auto_logout.json");

    public static final int AFK_SECONDS_MIN = 5;
    public static final int AFK_SECONDS_MAX = 600;

    public static boolean isModEnabled = true;

    public static float healthThreshold = 10.0f;

    public static String keyBinding = "key.keyboard.unknown";
    public static InputConstants.Key currentKeyBinding = InputConstants.getKey(keyBinding);

    public static boolean isEntityTrackingEnabled = true;
    public static int nearbyEntityCount = 1;
    public static double radius = 20.0;

    public static boolean showJoinMessage = false;

    public static int afkThresholdSeconds = 15;

    private ConfigManager() {}

    public static void loadConfig() {
        boolean updated = false;

        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData config = GSON.fromJson(reader, ConfigData.class);

                if (config == null) {
                    updated = true;
                } else {
                    if (config.isModEnabled != null) isModEnabled = config.isModEnabled; else updated = true;

                    if (config.healthThreshold != null) healthThreshold = config.healthThreshold; else updated = true;

                    if (config.keyBinding != null && !config.keyBinding.isBlank()) {
                        keyBinding = config.keyBinding;
                    } else {
                        updated = true;
                    }
                    currentKeyBinding = InputConstants.getKey(keyBinding);

                    if (config.isEntityTrackingEnabled != null) isEntityTrackingEnabled = config.isEntityTrackingEnabled; else updated = true;

                    if (config.nearbyEntityCount != null) nearbyEntityCount = config.nearbyEntityCount; else updated = true;

                    if (config.radius != null) radius = config.radius; else updated = true;

                    if (config.showJoinMessage != null) showJoinMessage = config.showJoinMessage; else updated = true;

                    if (config.afkThresholdSeconds != null) {
                        afkThresholdSeconds = clamp(config.afkThresholdSeconds, AFK_SECONDS_MIN, AFK_SECONDS_MAX);
                    } else {
                        updated = true;
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        } else {
            updated = true;
        }

        if (updated) saveConfig();
    }

    public static void saveConfig() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                ConfigData config = new ConfigData(
                        isModEnabled,
                        healthThreshold,
                        keyBinding,
                        isEntityTrackingEnabled,
                        nearbyEntityCount,
                        radius,
                        showJoinMessage,
                        clamp(afkThresholdSeconds, AFK_SECONDS_MIN, AFK_SECONDS_MAX)
                );
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public static int getAfkThresholdTicks() {
        return clamp(afkThresholdSeconds, AFK_SECONDS_MIN, AFK_SECONDS_MAX) * 20;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class ConfigData {
        Boolean isModEnabled;
        Float healthThreshold;
        String keyBinding;

        Boolean isEntityTrackingEnabled;
        Integer nearbyEntityCount;
        Double radius;

        Boolean showJoinMessage;

        Integer afkThresholdSeconds;

        ConfigData(
                Boolean isModEnabled,
                Float healthThreshold,
                String keyBinding,
                Boolean isEntityTrackingEnabled,
                Integer nearbyEntityCount,
                Double radius,
                Boolean showJoinMessage,
                Integer afkThresholdSeconds
        ) {
            this.isModEnabled = isModEnabled;
            this.healthThreshold = healthThreshold;
            this.keyBinding = keyBinding;
            this.isEntityTrackingEnabled = isEntityTrackingEnabled;
            this.nearbyEntityCount = nearbyEntityCount;
            this.radius = radius;
            this.showJoinMessage = showJoinMessage;
            this.afkThresholdSeconds = afkThresholdSeconds;
        }
    }
}
