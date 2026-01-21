package de.myownbrain.autoLogout.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientCommandRegistration {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("auto-logout")
                            .then(ClientCommandManager.literal("enable")
                                    .executes(ctx -> toggleAutoLogout(true)))
                            .then(ClientCommandManager.literal("disable")
                                    .executes(ctx -> toggleAutoLogout(false)))

                            .then(ClientCommandManager.literal("threshold")
                                    .executes(ctx -> showThreshold(ctx.getSource()))
                                    .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(0, 20))
                                            .executes(ctx -> setThreshold(
                                                    IntegerArgumentType.getInteger(ctx, "value")))
                                    )
                            )

                            .then(ClientCommandManager.literal("afk-threshold-seconds")
                                    .executes(ctx -> showAfkThreshold(ctx.getSource()))
                                    .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(5, 600))
                                            .executes(ctx -> setAfkThreshold(
                                                    IntegerArgumentType.getInteger(ctx, "value")))
                                    )
                            )

                            .then(ClientCommandManager.literal("entity-tracking")
                                    .then(ClientCommandManager.literal("enable")
                                            .executes(ctx -> toggleEntityTracking(true)))
                                    .then(ClientCommandManager.literal("disable")
                                            .executes(ctx -> toggleEntityTracking(false)))
                                    .then(ClientCommandManager.literal("entity-count")
                                            .executes(ctx -> showNearbyEntityCount(ctx.getSource()))
                                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(1, 10))
                                                    .executes(ctx -> setNearbyEntityCount(
                                                            IntegerArgumentType.getInteger(ctx, "value")))))
                                    .then(ClientCommandManager.literal("radius")
                                            .executes(ctx -> showTrackingRadius(ctx.getSource()))
                                            .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(1.0, 64.0))
                                                    .executes(ctx -> setTrackingRadius(
                                                            DoubleArgumentType.getDouble(ctx, "value")))))
                            )

                            .then(ClientCommandManager.literal("join-message")
                                    .then(ClientCommandManager.literal("enable")
                                            .executes(ctx -> toggleJoinMessage(true)))
                                    .then(ClientCommandManager.literal("disable")
                                            .executes(ctx -> toggleJoinMessage(false)))
                            )

                            .then(ClientCommandManager.literal("help")
                                    .executes(ctx -> showHelp(ctx.getSource())))
            );
        });
    }


    private static int toggleAutoLogout(boolean enable) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.isModEnabled = enable;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Auto Logout ")
                        .append(Component.literal(enable ? "enabled" : "disabled")
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(enable ? ChatFormatting.GREEN : ChatFormatting.RED)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int toggleEntityTracking(boolean enable) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.isEntityTrackingEnabled = enable;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Entity Tracking ")
                        .append(Component.literal(enable ? "enabled" : "disabled")
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(enable ? ChatFormatting.GREEN : ChatFormatting.RED)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int toggleJoinMessage(boolean enable) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.showJoinMessage = enable;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Join Message ")
                        .append(Component.literal(enable ? "enabled" : "disabled")
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(enable ? ChatFormatting.GREEN : ChatFormatting.RED)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }


    private static int showThreshold(FabricClientCommandSource source) {
        source.sendFeedback(
                Component.literal("Health threshold set to ")
                        .append(Component.literal(String.valueOf(ConfigManager.healthThreshold))
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD))
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setThreshold(int threshold) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.healthThreshold = threshold;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Health threshold set to ")
                        .append(Component.literal(String.valueOf(threshold))
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }


    private static int showAfkThreshold(FabricClientCommandSource source) {
        source.sendFeedback(
                Component.literal("AFK threshold set to ")
                        .append(Component.literal(ConfigManager.afkThresholdSeconds + " seconds")
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD))
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setAfkThreshold(int seconds) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.afkThresholdSeconds = seconds;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("AFK threshold set to ")
                        .append(Component.literal(seconds + " seconds")
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }


    private static int showNearbyEntityCount(FabricClientCommandSource source) {
        source.sendFeedback(
                Component.literal("Nearby Entity Count set to ")
                        .append(Component.literal(String.valueOf(ConfigManager.nearbyEntityCount))
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD))
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setNearbyEntityCount(int count) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.nearbyEntityCount = count;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Nearby Entity Count set to ")
                        .append(Component.literal(String.valueOf(count))
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int showTrackingRadius(FabricClientCommandSource source) {
        source.sendFeedback(
                Component.literal("Tracking Radius set to ")
                        .append(Component.literal(String.valueOf(ConfigManager.radius))
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD))
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setTrackingRadius(double radius) {
        if (Minecraft.getInstance().player == null) return 0;

        ConfigManager.radius = radius;
        ConfigManager.saveConfig();

        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Tracking Radius set to ")
                        .append(Component.literal(String.valueOf(radius))
                                .withStyle(style -> style.withBold(true)))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)),
                false
        );
        return Command.SINGLE_SUCCESS;
    }


    private static int showHelp(FabricClientCommandSource source) {
        source.sendFeedback(
                Component.literal("")
                        .append(Component.literal("\nAuto Logout Commands\n")
                                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
                        .append(Component.literal("/auto-logout enable").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(" - Enables the mod\n").withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("/auto-logout disable").withStyle(ChatFormatting.RED))
                        .append(Component.literal(" - Disables the mod\n").withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("/auto-logout threshold").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" - Sets health threshold\n").withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("/auto-logout afk-threshold-seconds <value>").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" - Sets AFK timeout in seconds\n").withStyle(ChatFormatting.WHITE))
        );
        return Command.SINGLE_SUCCESS;
    }
}
