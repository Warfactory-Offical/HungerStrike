package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncRequest(Boolean req) implements CustomPacketPayload
{
    public static final Type<SyncRequest> TYPE = new Type<>(new ResourceLocation(HungerStrike.MOD_ID, "sync_req"));

    public static final StreamCodec<FriendlyByteBuf, SyncRequest> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        SyncRequest::req,
        SyncRequest::new
    );

    public SyncRequest() {
        this(true);
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    public static void handleServer(final SyncRequest data, final IPayloadContext context) {
        Player player = context.player();
        if (player instanceof ServerPlayer sp) {
            NetworkHandler.sendTo(sp, new ConfigData());
            NetworkHandler.sendTo(sp, new PlayerData(ExtendedPlayer.get(sp)));
        }
    }
}
