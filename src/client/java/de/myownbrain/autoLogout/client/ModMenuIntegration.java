package de.myownbrain.autoLogout.client;

import de.myownbrain.autoLogout.client.integration.ModMenuIntegrationImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModMenuIntegration {
    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("modmenu")
                && FabricLoader.getInstance().isModLoaded("cloth-config2");
    }

    private static boolean wasKeyPressed = false;

    public static void handleToggleKey(MinecraftClient client) {
        boolean isPressed = ModMenuIntegrationImpl.isToggleKeyPressed();

        if (client.currentScreen != null) {
            wasKeyPressed = false;
            return;
        }

        if (isPressed && !wasKeyPressed) {
            ConfigManager.isModEnabled = !ConfigManager.isModEnabled;
            ConfigManager.saveConfig();

            assert client.player != null;
            client.player.sendMessage(
                    Text.literal("Auto Logout ")
                            .append(Text.literal(ConfigManager.isModEnabled ? "enabled" : "disabled")
                                    .styled(s -> s.withBold(true)))
                            .styled(s -> s.withColor(ConfigManager.isModEnabled ? Formatting.GREEN : Formatting.RED)),
                    false
            );
        }

        wasKeyPressed = isPressed;
    }
}
