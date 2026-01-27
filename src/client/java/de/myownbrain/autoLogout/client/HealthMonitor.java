package de.myownbrain.autoLogout.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.List;

public final class HealthMonitor {

    private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#");

    private static int afkTicks = 0;
    private static float lastYaw = 0f;
    private static float lastPitch = 0f;

    private static float healthWhenAfkStarted = -1f;
    private static boolean tookDamageWhileAfk = false;

    private HealthMonitor() {}

    public static void monitorPlayerHealth(Minecraft client) {
        if (!ConfigManager.isModEnabled) return;

        Player player = client.player;
        if (player == null) return;

        if (isPlayerActive(client, player)) {
            resetAfkState();
            return;
        }

        afkTicks++;

        // mark baseline
        if (afkTicks == 1) {
            healthWhenAfkStarted = player.getHealth();
            tookDamageWhileAfk = false;
        }

        // detect damage while AFK 
        float currentHealth = player.getHealth();
        if (healthWhenAfkStarted >= 0 && currentHealth < healthWhenAfkStarted) {
            tookDamageWhileAfk = true;
            healthWhenAfkStarted = currentHealth;
        }

        // disconnect only if: AFK long enough AND health low AND took damage while AFK
        if (afkTicks >= ConfigManager.getAfkThresholdTicks()
                && currentHealth <= ConfigManager.healthThreshold
                && tookDamageWhileAfk) {
            disconnectPlayer(client, player);
        }
    }

    private static boolean isPlayerActive(Minecraft client, Player player) {
        boolean input =
                client.options.keyUp.isDown()
                        || client.options.keyDown.isDown()
                        || client.options.keyLeft.isDown()
                        || client.options.keyRight.isDown()
                        || client.options.keyJump.isDown()
                        || client.options.keySprint.isDown()
                        || client.options.keyAttack.isDown()
                        || client.options.keyUse.isDown()
                        || client.options.keyPlayerList.isDown()
                        || client.options.keyInventory.isDown()
                        || client.options.keyChat.isDown();

        float yaw = player.getYRot();
        float pitch = player.getXRot();

        boolean rotated = (yaw != lastYaw) || (pitch != lastPitch);

        lastYaw = yaw;
        lastPitch = pitch;

        return input || rotated;
    }

    private static void resetAfkState() {
        afkTicks = 0;
        healthWhenAfkStarted = -1f;
        tookDamageWhileAfk = false;
    }

    private static void disconnectPlayer(Minecraft client, Player player) {
        if (client.getConnection() == null) return;

        String playerX = formatCoord(player.getX());
        String playerY = formatCoord(player.getY());
        String playerZ = formatCoord(player.getZ());

        MutableComponent text = Component.literal(
                        "You were disconnected by Auto Logout due to taking damage while AFK.\n\n")
                .withStyle(style -> style.withBold(true).withColor(ChatFormatting.RED))
                .append(Component.literal(
                                "Health: " + HEALTH_FORMAT.format(player.getHealth()) +
                                        " (≈" + Math.floor(player.getHealth() / 2) + " Hearts)\n")
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)))
                .append(Component.literal(
                                "Coordinates: %s %s %s".formatted(playerX, playerY, playerZ))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)));

        if (ConfigManager.isEntityTrackingEnabled) {
            List<String> entityNames = NearestEntityFinder.getNearestEntities().stream()
                    .map(e -> e.getName().getString())
                    .toList();
            text.append(Component.literal("\nNearby Entities: " + String.join(", ", entityNames))
                    .withStyle(style -> style.withColor(ChatFormatting.GOLD)));
        }

        text.append(Component.literal("\n\nYou're safe, for now.")
                .withStyle(style -> style.withColor(ChatFormatting.WHITE)));

        client.gui.getChat().addMessage(text);

        client.getConnection().getConnection().disconnect(text);

        resetAfkState();
    }

    private static String formatCoord(double v) {
        double rounded = (v >= 0) ? Math.floor(v) : Math.ceil(v);
        return COORDS_FORMAT.format(rounded);
    }
}
