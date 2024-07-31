package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record PlayerData(Boolean onStrike) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(HungerStrike.MOD_ID, "player_data");

    public PlayerData(ExtendedPlayer player) {
        this(player.isOnHungerStrike());
    }

    public PlayerData(final FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    @Override
    public void write (FriendlyByteBuf buffer) {
        buffer.writeBoolean(onStrike);
    }

    @Override
    public ResourceLocation id () {
        return ID;
    }

    public static void handleClient(final PlayerData data, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            if (context.player().isPresent()) {
                ExtendedPlayer ep = ExtendedPlayer.get(context.player().get());
                ep.loadState(data.onStrike());
            }
        }).exceptionally(e -> null);
    }
}
