package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SyncRequest() implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(HungerStrike.MOD_ID, "sync_req");

    public SyncRequest(final FriendlyByteBuf buffer) {
        this();
    }

    @Override
    public void write (FriendlyByteBuf buffer) {

    }

    @Override
    public ResourceLocation id () {
        return ID;
    }

    public static void handleServer(final SyncRequest data, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            if (context.player().isPresent()) {
                Player player = context.player().get();
                if (player instanceof ServerPlayer sp) {
                    NetworkHandler.sendTo(sp, new ConfigData());
                    NetworkHandler.sendTo(sp, new PlayerData(ExtendedPlayer.get(sp)));
                }
            }
        }).exceptionally(e -> null);
    }
}
