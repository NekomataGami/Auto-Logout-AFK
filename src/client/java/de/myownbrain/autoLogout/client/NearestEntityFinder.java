package de.myownbrain.autoLogout.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NearestEntityFinder {
    private static List<Entity> nearestEntity = new ArrayList<>();

    public static void updateNearestEntities(MinecraftClient client, double radius) {
        if (client.world == null || client.player == null) return;

        Vec3d playerPos = client.player.getEntityPos();
        Box searchBox = new Box(
                playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
        );

        nearestEntity = client.world.getOtherEntities(client.player, searchBox).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .sorted(Comparator.comparingDouble(entity -> playerPos.squaredDistanceTo(entity.getEntityPos())))
                .limit(ConfigManager.nearbyEntityCount)
                .toList();
    }

    public static List<Entity> getNearestEntities() {
        return nearestEntity;
    }
}
