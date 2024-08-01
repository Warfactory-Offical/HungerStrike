package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.List;

public class PlayerHandler
{
    //private static final Map<GameProfile, Map<String, CompoundTag>> dataStore = new HashMap<>();

    public static List<Player> getStrikingPlayers (MinecraftServer server) {
        return getPlayers(server, true);
    }

    public static List<Player> getNonStrikingPlayers (MinecraftServer server) {
        return getPlayers(server, false);
    }

    private static List<Player> getPlayers (MinecraftServer server, boolean isStriking) {
        List<Player> players = new ArrayList<>();
        for (ServerPlayer playerEnt : server.getPlayerList().getPlayers()) {
            ExtendedPlayer playerExt = ExtendedPlayer.get(playerEnt);
            if (playerExt != null && playerExt.isOnHungerStrike() == isStriking)
                players.add(playerEnt);
        }

        return players;
    }

    /*public void storeData (Player player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            CompoundTag data = new CompoundTag();
            playerExt.saveNBTData(data);
            
            storeData(player, "HungerStrike", data);
        }
    }

    public void storeData (Player player, String name, CompoundTag data) {
        storeData(player.getGameProfile(), name, data);
    }

    public void storeData (GameProfile profile, String name, CompoundTag data) {
        Map<String, CompoundTag> store = dataStore.get(profile);
        if (store == null) {
            store = new HashMap<>();
            dataStore.put(profile, store);
        }

        store.put(name, data);
    }

    public void restoreData (Player player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);

        if (playerExt != null) {
            CompoundTag data = getData(player, "HungerStrike");
            if (data != null)
                playerExt.loadNBTData(data);
        }
    }*/

    /*public CompoundTag getData (Player player, String name) {
        return getData(player.getGameProfile(), name);
    }

    public CompoundTag getData (GameProfile profile, String name) {
        Map<String, CompoundTag> store = dataStore.get(profile);
        if (store == null)
            return null;

        return store.remove(name);
    }*/

    public void tickStart (Player player, LogicalSide side) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            playerExt.tickStart();
    }

    public void tickEnd (Player player, LogicalSide side) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            playerExt.tickEnd(side);
    }

    public boolean isOnHungerStrike (Player player) {
        ExtendedPlayer playerExt = ExtendedPlayer.get(player);
        if (playerExt != null)
            return playerExt.isOnHungerStrike();

        return false;
    }
}
