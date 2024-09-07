package com.texelsaurus.minecraft.hungerstrike.network;

import com.texelsaurus.minecraft.hungerstrike.ModConstants;
import com.texelsaurus.minecraft.hungerstrike.ModServices;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record SyncRequest(Boolean req) implements CommonPacket
{
    public static final Type<SyncRequest> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "sync_req"));

    public static final StreamCodec<FriendlyByteBuf, SyncRequest> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        SyncRequest::req,
        SyncRequest::new
    );

    public SyncRequest () {
        this(true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    @Override
    public void handleMessage (Player player, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            if (player instanceof ServerPlayer sp) {
                ModServices.NETWORK.sendToPlayer(new ConfigData(), sp);
                ModServices.NETWORK.sendToPlayer(new PlayerData(ModServices.PLAYER_HANDLER.getExtendedPlayer(sp)), sp);
            }
        });
    }
}
