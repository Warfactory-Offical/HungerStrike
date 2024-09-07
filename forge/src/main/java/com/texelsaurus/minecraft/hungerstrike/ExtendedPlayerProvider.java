package com.texelsaurus.minecraft.hungerstrike;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtendedPlayerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>
{
    public static Capability<ForgeExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final ForgeExtendedPlayer extendedPlayer;
    private LazyOptional<?> playerHandler;

    public ExtendedPlayerProvider(Player player) {
        extendedPlayer = new ForgeExtendedPlayer(player);
        playerHandler = LazyOptional.of(() -> extendedPlayer);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction facing) {
        return capability == EXTENDED_PLAYER_CAPABILITY ? playerHandler.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compound = new CompoundTag();
        extendedPlayer.saveNBTData(compound);

        return compound;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        extendedPlayer.loadNBTData(nbt);
    }
}
