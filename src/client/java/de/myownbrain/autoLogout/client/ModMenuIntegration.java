package de.myownbrain.autoLogout.client.integration;

import com.mojang.blaze3d.platform.InputConstants;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.myownbrain.autoLogout.client.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegrationImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Auto Logout Configuration"));

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Auto Logout"), ConfigManager.isModEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.isModEnabled = newValue;
                    ConfigManager.saveConfig();
                })
                .build()
        );

        general.addEntry(entryBuilder.startFloatField(Component.literal("Health threshold (2 Health = 1 Heart)"), ConfigManager.healthThreshold)
                .setDefaultValue(4.0f)
                .setMin(0.0f)
                .setMax(20.0f)
                .setSaveConsumer(newValue -> {
                    ConfigManager.healthThreshold = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Component.literal("When health drops below the threshold you get disconnected. (2 Health = 1 Heart)"))
                .build()
        );

        general.addEntry(entryBuilder.startIntField(Component.literal("AFK Threshold (seconds)"), ConfigManager.afkThresholdSeconds)
                .setDefaultValue(15)
                .setMin(5)
                .setMax(600)
                .setSaveConsumer(newValue -> {
                    ConfigManager.afkThresholdSeconds = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Component.literal("Time you must be AFK before Auto Logout can trigger.\nOnly disconnects if you take damage while AFK and HP is below the threshold."))
                .build()
        );

        general.addEntry(entryBuilder.startKeyCodeField(Component.literal("Toggle Auto Logout"), ConfigManager.currentKeyBinding)
                .setDefaultValue(InputConstants.UNKNOWN)
                .setKeySaveConsumer(newKey -> {
                    ConfigManager.currentKeyBinding = newKey;
                    ConfigManager.keyBinding = newKey.getName();
                    ConfigManager.saveConfig();
                })
                .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Entity Tracking"), ConfigManager.isEntityTrackingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.isEntityTrackingEnabled = newValue;
                    ConfigManager.saveConfig();
                })
                .build()
        );

        general.addEntry(entryBuilder.startIntField(Component.literal("Nearby Entity Count"), ConfigManager.nearbyEntityCount)
                .setDefaultValue(5)
                .setMin(1)
                .setMax(10)
                .setSaveConsumer(newValue -> {
                    ConfigManager.nearbyEntityCount = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Component.literal("Amount of the Nearby Entities displayed in the disconnect screen."))
                .build()
        );

        general.addEntry(entryBuilder.startDoubleField(Component.literal("Tracking Radius"), ConfigManager.radius)
                .setDefaultValue(20.0)
                .setMin(1.0)
                .setMax(64.0)
                .setSaveConsumer(newValue -> {
                    ConfigManager.radius = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Component.literal("Radius in which entities are tracked."))
                .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Show message when joining a world"), ConfigManager.showJoinMessage)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.showJoinMessage = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Component.literal("Shows a message when joining a world telling whether the Mod and Entity Tracking are enabled."))
                .build()
        );

        builder.setSavingRunnable(ConfigManager::saveConfig);
        return builder.build();
    }

    public static boolean isToggleKeyPressed() {
        if (ConfigManager.currentKeyBinding == null) {
            ConfigManager.currentKeyBinding = InputConstants.UNKNOWN;
        }
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), ConfigManager.currentKeyBinding.getValue());
    }
}
