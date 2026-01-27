package de.myownbrain.autoLogout.client;

import de.myownbrain.autoLogout.client.integration.ModMenuIntegrationImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class ModMenuIntegration {

    private static boolean wasKeyPressed = false;

    private ModMenuIntegration() {}

    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("modmenu")
                && FabricLoader.getInstance().isModLoaded("cloth-config2");
    }

    public static void handleToggleKey(Minecraft client) {
        boolean isPressed = ModMenuIntegrationImpl.isToggleKeyPressed();

        if (client.screen != null) {
            wasKeyPressed = false;
            return;
        }

        if (isPressed && !wasKeyPressed) {
            ConfigManager.isModEnabled = !ConfigManager.isModEnabled;
            ConfigManager.saveConfig();

            if (client.player != null) {
                client.player.displayClientMessage(
                        Component.literal("Auto Logout ")
                                .append(Component.literal(ConfigManager.isModEnabled ? "enabled" : "disabled")
                                        .withStyle(s -> s.withBold(true)))
                                .withStyle(s -> s.withColor(ConfigManager.isModEnabled ? ChatFormatting.GREEN : ChatFormatting.RED)),
                        false
                );
            }
        }

        wasKeyPressed = isPressed;
    }
}
