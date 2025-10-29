package de.myownbrain.autoLogout.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.myownbrain.autoLogout.client.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class ModMenuIntegrationImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Auto Logout Configuration"));

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General Settings"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Auto Logout"), ConfigManager.isModEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.isModEnabled = newValue;
                    ConfigManager.saveConfig();
                })
                .build()
        );

        general.addEntry(entryBuilder.startFloatField(Text.literal("Health threshold (2 Health = 1 Heart)"), ConfigManager.healthThreshold)
                .setDefaultValue(4.0f)
                .setMin(0.0f)
                .setMax(20.0f)
                .setSaveConsumer(newValue -> {
                    ConfigManager.healthThreshold = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Text.literal("When health drops below the threshold you get disconnected. (2 Health = 1 Heart)"))
                .build()
        );

        general.addEntry(entryBuilder.startKeyCodeField(Text.literal("Toggle Auto Logout"), ConfigManager.currentKeyBinding)
                .setDefaultValue(InputUtil.UNKNOWN_KEY)
                .setKeySaveConsumer(newKey -> {
                    ConfigManager.currentKeyBinding = newKey;
                    ConfigManager.keyBinding = newKey.getTranslationKey();
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Entity Tracking"), ConfigManager.isEntityTrackingEnabled)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.isEntityTrackingEnabled = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.literal("Nearby Entity Count"), ConfigManager.nearbyEntityCount)
                .setDefaultValue(5)
                .setMin(1)
                .setMax(10)
                .setSaveConsumer(newValue -> {
                    ConfigManager.nearbyEntityCount = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Text.literal("Amount of the Nearby Entities displayed in the disconnect screen."))
                .build()
        );

        general.addEntry(entryBuilder.startDoubleField(Text.literal("Tracking Radius"), ConfigManager.radius)
                .setDefaultValue(20.0)
                .setMin(1.0)
                .setMax(64.0)
                .setSaveConsumer(newValue -> {
                    ConfigManager.radius = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Text.literal("Radius in which entities are tracked."))
                .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show message when joining a world"), ConfigManager.showJoinMessage)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.showJoinMessage = newValue;
                    ConfigManager.saveConfig();
                })
                .setTooltip(Text.literal("Shows a message when joining a world telling whether the Mod and Entity Tracking are enabled."))
                .build()
        );

        builder.setSavingRunnable(ConfigManager::saveConfig);

        return builder.build();
    }

    public static boolean isToggleKeyPressed() {
        if (ConfigManager.currentKeyBinding == null) {
            ConfigManager.currentKeyBinding = InputUtil.UNKNOWN_KEY;
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), ConfigManager.currentKeyBinding.getCode());
    }
}
