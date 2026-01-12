package com.maxwell.cyber_ware_port.common.command;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CyberwareCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cyberware")
                .requires(source -> source.hasPermission(2)) // OP権限レベル2以上
                .then(Commands.literal("clear")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> clearCyberware(ctx, EntityArgument.getPlayer(ctx, "target")))))
                .then(Commands.literal("heal")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> healCyberware(ctx, EntityArgument.getPlayer(ctx, "target")))))
                .then(Commands.literal("admin")
                        .then(Commands.literal("reset_human")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> resetToHuman(ctx, EntityArgument.getPlayer(ctx, "target")))))
                )
        );
    }

    private static int resetToHuman(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            data.resetToHuman();
            data.recalculateCapacity(player);
            data.syncToClient(player);
            ctx.getSource().sendSuccess(() ->
                    Component.literal("Successfully reset " + player.getName().getString() + " to human state."), true);
            player.sendSystemMessage(Component.literal("Your cyberware has been reset by an administrator."));
        });
        return 1;
    }

    private static int clearCyberware(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            data.resetToHuman();
            data.syncToClient(player);
            ctx.getSource().sendSuccess(() -> Component.literal("Reset cyberware for " + player.getName().getString()), true);
        });
        return 1;
    }

    private static int healCyberware(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            data.syncToClient(player);
            ctx.getSource().sendSuccess(() -> Component.literal("Cyberware energy/status restored for " + player.getName().getString()), true);
        });
        return 1;
    }
}