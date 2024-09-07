package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.network.PlayerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

@AutoRegisterCapability
public class ForgeExtendedPlayer extends ExtendedPlayer
{
    public static final ResourceLocation EXTENDED_PLAYER_KEY = ResourceLocation.fromNamespaceAndPath(HungerStrike.MOD_ID, "extended_player");

    public static Capability<ForgeExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private boolean hungerStrikeEnabled;

    public ForgeExtendedPlayer (Player player) {
        super(player);
        this.hungerStrikeEnabled = false;
    }

    public static ForgeExtendedPlayer get (Player player) {
        if (EXTENDED_PLAYER_CAPABILITY == null || player == null)
            return null;

        return player.getCapability(EXTENDED_PLAYER_CAPABILITY, null).orElse(null);
    }

    public void saveNBTData(CompoundTag compound) {
        compound.putBoolean("Enabled", hungerStrikeEnabled);
    }

    public void loadNBTData(CompoundTag compound) {
        hungerStrikeEnabled = compound.getBoolean("Enabled");
    }

    @Override
    public void enableHungerStrike (boolean enable) {
        if (hungerStrikeEnabled != enable) {
            hungerStrikeEnabled = enable;

            if (player instanceof ServerPlayer playerMP)
                ModServices.NETWORK.sendToPlayer(new PlayerData(this), playerMP);
        }
    }

    @Override
    public void loadState (boolean hungerStrikeEnabled) {
        this.hungerStrikeEnabled = hungerStrikeEnabled;
    }

    @Override
    public boolean isOnHungerStrike () {
        return hungerStrikeEnabled;
    }
}
