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
  private static final int AFK_THRESHOLD_TICKS = 30 * 20;

  public static void monitorPlayerHealth(Minecraft client) {
    if (!ConfigManager.isModEnabled || client.player == null) return;

    Player player = client.player;

    boolean moving = isPlayerMoving(client);
    if (!moving) afkTicks++;
    else afkTicks = 0;

    if (afkTicks >= AFK_THRESHOLD_TICKS &&
      player.getHealth() <= ConfigManager.healthThreshold) {
      disconnectPlayer(client);
    }
  }

  private static float lastYaw, lastPitch;

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
        "You were disconnected by Auto Logout due to low health while AFK.\n\n")
      .withStyle(style -> style.withBold(true).withColor(ChatFormatting.RED))
      .append(Component.literal(
          "Health: " + healthFormat.format(player.getHealth()) +
          " (≈" + Math.floor(player.getHealth() / 2) + " Hearts)\n")
        .withStyle(style -> style.withColor(ChatFormatting.GOLD)))
      .append(Component.literal(
          String.format("Coordinates: %s %s %s", playerX, playerY, playerZ))
        .withStyle(style -> style.withColor(ChatFormatting.GOLD)));

    if (ConfigManager.isEntityTrackingEnabled) {
      List < String > entityNames = NearestEntityFinder.getNearestEntities().stream()
        .map(e -> e.getName().getString())
        .toList();
      text.append(Component.literal("\nNearby Entities: " + String.join(", ", entityNames))
        .withStyle(style -> style.withColor(ChatFormatting.GOLD)));
    }

    text.append(Component.literal("\n\nAuto Logout got disabled.")
      .withStyle(style -> style.withColor(ChatFormatting.WHITE)));

    client.getConnection().getConnection().disconnect(text);

    ConfigManager.isModEnabled = false;
    ConfigManager.saveConfig();
    afkTicks = 0;
  }
}
