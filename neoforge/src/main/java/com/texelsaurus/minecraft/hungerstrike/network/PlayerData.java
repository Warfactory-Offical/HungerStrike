package com.texelsaurus.minecraft.hungerstrike.network;

import com.texelsaurus.minecraft.hungerstrike.ExtendedPlayer;
import com.texelsaurus.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerData(Boolean onStrike) implements CustomPacketPayload
{
    public static final Type<PlayerData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HungerStrike.MOD_ID, "player_data"));

    public static final StreamCodec<FriendlyByteBuf, PlayerData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        PlayerData::onStrike,
        PlayerData::new
    );

    public PlayerData(ExtendedPlayer player) {
        this(player.isOnHungerStrike());
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    public static void handleClient(final PlayerData data, final IPayloadContext context) {
        ExtendedPlayer ep = ExtendedPlayer.get(context.player());
        ep.loadState(data.onStrike());
    }
}
