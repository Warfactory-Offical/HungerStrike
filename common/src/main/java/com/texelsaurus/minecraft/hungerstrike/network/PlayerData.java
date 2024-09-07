package com.texelsaurus.minecraft.hungerstrike.network;

import com.texelsaurus.minecraft.hungerstrike.ExtendedPlayer;
import com.texelsaurus.minecraft.hungerstrike.ModConstants;
import com.texelsaurus.minecraft.hungerstrike.ModServices;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record PlayerData(Boolean onStrike) implements CommonPacket
{
    public static final Type<PlayerData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "player_data"));

    public static final StreamCodec<FriendlyByteBuf, PlayerData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        PlayerData::onStrike,
        PlayerData::new
    );

    public PlayerData (ExtendedPlayer player) {
        this(player.isOnHungerStrike());
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    @Override
    public void handleMessage (Player player, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            ExtendedPlayer ep = ModServices.PLAYER_HANDLER.getExtendedPlayer(player);
            ep.loadState(onStrike);
        });
    }
}
