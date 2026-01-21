package de.myownbrain.autoLogout.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.List;

public class HealthMonitor {

    private static final DecimalFormat healthFormat = new DecimalFormat("#.##");
    private static final DecimalFormat coordsFormat = new DecimalFormat("#");

    private static int afkTicks = 0;

    private static float lastYaw, lastPitch;

    private static float healthWhenAfkStarted = -1f;
    private static boolean tookDamageWhileAfk = false;

    public static void monitorPlayerHealth(Minecraft client) {
        if (!ConfigManager.isModEnabled || client.player == null) return;

        Player player = client.player;
        boolean moving = isPlayerMoving(client);

        if (moving) {
            afkTicks = 0;
            healthWhenAfkStarted = -1f;
            tookDamageWhileAfk = false;
            return;
        }

        afkTicks++;

        if (afkTicks == 1) {
            healthWhenAfkStarted = player.getHealth();
            tookDamageWhileAfk = false;
        }

        if (healthWhenAfkStarted >= 0 && player.getHealth() < healthWhenAfkStarted) {
            tookDamageWhileAfk = true;
            healthWhenAfkStarted = player.getHealth(); 
        }

        if (afkTicks >= ConfigManager.getAfkThresholdTicks()
                && player.getHealth() <= ConfigManager.healthThreshold
                && tookDamageWhileAfk) {
            disconnectPlayer(client);
        }
    }

    private static boolean isPlayerMoving(Minecraft client) {
        if (client.player == null) return false;

        boolean input =
                client.options.keyUp.isDown() ||
                client.options.keyDown.isDown() ||
                client.options.keyLeft.isDown() ||
                client.options.keyRight.isDown() ||
                client.options.keyJump.isDown() ||
                client.options.keySprint.isDown() ||
                client.options.keyAttack.isDown() ||
                client.options.keyUse.isDown();

        float yaw = client.player.getYRot();
        float pitch = client.player.getXRot();

        boolean rotated = yaw != lastYaw || pitch != lastPitch;

        lastYaw = yaw;
        lastPitch = pitch;

        return input || rotated;
    }

    private static void disconnectPlayer(Minecraft client) {
        if (client.player == null || client.getConnection() == null) return;

        Player player = client.player;

        String playerX = coordsFormat.format(player.getX() >= 0 ? Math.floor(player.getX()) : Math.ceil(player.getX()));
        String playerY = coordsFormat.format(player.getY() >= 0 ? Math.floor(player.getY()) : Math.ceil(player.getY()));
        String playerZ = coordsFormat.format(player.getZ() >= 0 ? Math.floor(player.getZ()) : Math.ceil(player.getZ()));

        MutableComponent text = Component.literal(
                        "You were disconnected by Auto Logout due to taking damage while AFK.\n\n")
                .withStyle(style -> style.withBold(true).withColor(ChatFormatting.RED))
                .append(Component.literal(
                                "Health: " + healthFormat.format(player.getHealth()) +
                                        " (≈" + Math.floor(player.getHealth() / 2) + " Hearts)\n")
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)))
                .append(Component.literal(
                                String.format("Coordinates: %s %s %s", playerX, playerY, playerZ))
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

        afkTicks = 0;
        healthWhenAfkStarted = -1f;
        tookDamageWhileAfk = false;
    }
}
