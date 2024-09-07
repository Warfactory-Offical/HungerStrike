package com.texelsaurus.minecraft.hungerstrike.service;

import com.texelsaurus.minecraft.hungerstrike.ModServices;
import com.texelsaurus.minecraft.hungerstrike.network.CommonPacket;
import com.texelsaurus.minecraft.hungerstrike.network.ConfigData;
import com.texelsaurus.minecraft.hungerstrike.network.PlayerData;
import com.texelsaurus.minecraft.hungerstrike.network.SyncRequest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface CommonNetworking
{
    static void init() {
        registerPacket(SyncRequest.TYPE, SyncRequest.STREAM_CODEC, false);
        registerPacket(ConfigData.TYPE, ConfigData.STREAM_CODEC, true);
        registerPacket(PlayerData.TYPE, PlayerData.STREAM_CODEC, true);
    }

    private static <B extends FriendlyByteBuf, P extends CommonPacket> void registerPacket(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound) {
        ModServices.NETWORK.registerPacketInternal(payloadType, codec, clientBound);
    }

    <B extends FriendlyByteBuf, P extends CommonPacket> void registerPacketInternal(CustomPacketPayload.Type<P> payloadType, StreamCodec<B, P> codec, boolean clientBound);

    void sendToPlayer(CommonPacket packet, ServerPlayer player);

    void sendToServer(CommonPacket packet);
}
