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

public final class ClientCommandRegistration {

    private ClientCommandRegistration() {}

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
                                            .executes(ctx -> setThreshold(IntegerArgumentType.getInteger(ctx, "value")))
                                    )
                            )

                            .then(ClientCommandManager.literal("afk-threshold-seconds")
                                    .executes(ctx -> showAfkThreshold(ctx.getSource()))
                                    .then(ClientCommandManager.argument("value",
                                                    IntegerArgumentType.integer(ConfigManager.AFK_SECONDS_MIN, ConfigManager.AFK_SECONDS_MAX))
                                            .executes(ctx -> setAfkThreshold(IntegerArgumentType.getInteger(ctx, "value")))
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
                                                    .executes(ctx -> setNearbyEntityCount(IntegerArgumentType.getInteger(ctx, "value")))
                                            )
                                    )

                                    .then(ClientCommandManager.literal("radius")
                                            .executes(ctx -> showTrackingRadius(ctx.getSource()))
                                            .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(1.0, 64.0))
                                                    .executes(ctx -> setTrackingRadius(DoubleArgumentType.getDouble(ctx, "value")))
                                            )
                                    )
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
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.isModEnabled = enable;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(statusLine("Auto Logout", enable), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int toggleEntityTracking(boolean enable) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.isEntityTrackingEnabled = enable;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(statusLine("Entity Tracking", enable), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int toggleJoinMessage(boolean enable) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.showJoinMessage = enable;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(statusLine("Join Message", enable), false);
        return Command.SINGLE_SUCCESS;
    }


    private static int showThreshold(FabricClientCommandSource source) {
        source.sendFeedback(goldLine("Health threshold set to ", String.valueOf(ConfigManager.healthThreshold)));
        return Command.SINGLE_SUCCESS;
    }

    private static int showAfkThreshold(FabricClientCommandSource source) {
        source.sendFeedback(goldLine("AFK threshold set to ", ConfigManager.afkThresholdSeconds + " seconds"));
        return Command.SINGLE_SUCCESS;
    }

    private static int showNearbyEntityCount(FabricClientCommandSource source) {
        source.sendFeedback(goldLine("Nearby Entity Count set to ", String.valueOf(ConfigManager.nearbyEntityCount)));
        return Command.SINGLE_SUCCESS;
    }

    private static int showTrackingRadius(FabricClientCommandSource source) {
        source.sendFeedback(goldLine("Tracking Radius set to ", String.valueOf(ConfigManager.radius)));
        return Command.SINGLE_SUCCESS;
    }


    private static int setThreshold(int threshold) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.healthThreshold = threshold;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(goldLine("Health threshold set to ", String.valueOf(threshold)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setAfkThreshold(int seconds) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.afkThresholdSeconds = seconds;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(goldLine("AFK threshold set to ", seconds + " seconds"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setNearbyEntityCount(int count) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.nearbyEntityCount = count;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(goldLine("Nearby Entity Count set to ", String.valueOf(count)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setTrackingRadius(double radius) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        ConfigManager.radius = radius;
        ConfigManager.saveConfig();

        mc.player.displayClientMessage(goldLine("Tracking Radius set to ", String.valueOf(radius)), false);
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
                        .append(Component.literal("/auto-logout threshold <value>").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" - Sets health threshold\n").withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("/auto-logout afk-threshold-seconds <value>").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" - Sets AFK timeout in seconds\n").withStyle(ChatFormatting.WHITE))
        );
        return Command.SINGLE_SUCCESS;
    }


    private static Component goldLine(String label, String value) {
        return Component.literal(label)
                .append(Component.literal(value).withStyle(style -> style.withBold(true)))
                .withStyle(style -> style.withColor(ChatFormatting.GOLD));
    }

    private static Component statusLine(String label, boolean enabled) {
        return Component.literal(label + " ")
                .append(Component.literal(enabled ? "enabled" : "disabled").withStyle(style -> style.withBold(true)))
                .withStyle(style -> style.withColor(enabled ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}
