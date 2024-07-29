package com.jaquadro.minecraft.hungerstrike;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtendedPlayerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final ExtendedPlayer extendedPlayer;
    private LazyOptional<?> playerHandler;

    public ExtendedPlayerProvider(Player player) {
        extendedPlayer = new ExtendedPlayer(player);
        playerHandler = LazyOptional.of(() -> extendedPlayer);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> capability, final @Nullable Direction facing) {
        return capability == ExtendedPlayerHandler.EXTENDED_PLAYER_CAPABILITY ? playerHandler.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        extendedPlayer.saveNBTData(compound);
        
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        extendedPlayer.loadNBTData(nbt);
    }
}
