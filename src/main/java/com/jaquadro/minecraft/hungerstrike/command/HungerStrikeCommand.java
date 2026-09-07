package com.jaquadro.minecraft.hungerstrike.command;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrikeHandler;
import com.jaquadro.minecraft.hungerstrike.HungerStrikeState;
import com.jaquadro.minecraft.hungerstrike.network.HungerStrikeNetwork;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class HungerStrikeCommand {
    private HungerStrikeCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hungerstrike")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("list")
                        .executes(c -> listPlayers(c.getSource())))
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .suggests((c, builder) -> SharedSuggestionProvider.suggest(
                                        namesOf(HungerStrikeHandler.getNonStrikingPlayers(c.getSource().getServer())), builder))
                                .executes(c -> setStrike(c.getSource(),
                                        GameProfileArgument.getGameProfiles(c, "targets"), true))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .suggests((c, builder) -> SharedSuggestionProvider.suggest(
                                        namesOf(HungerStrikeHandler.getStrikingPlayers(c.getSource().getServer())), builder))
                                .executes(c -> setStrike(c.getSource(),
                                        GameProfileArgument.getGameProfiles(c, "targets"), false))))
                .then(Commands.literal("mode")
                        .executes(c -> reportMode(c.getSource())))
                .then(Commands.literal("setmode")
                        .then(Commands.literal("none").executes(c -> setMode(c.getSource(), Config.Mode.NONE)))
                        .then(Commands.literal("list").executes(c -> setMode(c.getSource(), Config.Mode.LIST)))
                        .then(Commands.literal("all").executes(c -> setMode(c.getSource(), Config.Mode.ALL)))));
    }

    private static int listPlayers(CommandSourceStack source) {
        List<String> names = namesOf(HungerStrikeHandler.getStrikingPlayers(source.getServer()));
        if (names.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.hungerstrike.list.none"), false);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.hungerstrike.list",
                    names.size(), String.join(", ", names)), false);
        }
        return names.size();
    }

    private static int setStrike(CommandSourceStack source, Collection<NameAndId> targets, boolean onStrike) {
        int changed = 0;
        String messageKey = onStrike ? "commands.hungerstrike.add.success" : "commands.hungerstrike.remove.success";

        for (NameAndId target : targets) {
            ServerPlayer player = source.getServer().getPlayerList().getPlayer(target.id());
            if (player == null || HungerStrikeState.isOnStrike(player) == onStrike) {
                continue;
            }
            HungerStrikeState.setOnStrike(player, onStrike);
            source.sendSuccess(() -> Component.translatable(messageKey, target.name()), true);
            changed++;
        }

        return changed;
    }

    private static int reportMode(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable(modeKey("commands.hungerstrike.mode.", Config.mode())), false);
        return 1;
    }

    private static int setMode(CommandSourceStack source, Config.Mode mode) {
        Config.setMode(mode);
        HungerStrikeNetwork.broadcastMode(source.getServer());
        source.sendSuccess(() -> Component.translatable(modeKey("commands.hungerstrike.setmode.", mode)), true);
        return 1;
    }

    private static String modeKey(String prefix, Config.Mode mode) {
        return prefix + mode.name().toLowerCase(java.util.Locale.ROOT);
    }

    private static List<String> namesOf(List<ServerPlayer> players) {
        List<String> names = new ArrayList<>(players.size());
        for (ServerPlayer player : players) {
            names.add(player.getGameProfile().name());
        }
        return names;
    }
}
