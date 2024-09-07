package com.texelsaurus.minecraft.hungerstrike.network;

import com.texelsaurus.minecraft.hungerstrike.ModConstants;
import com.texelsaurus.minecraft.hungerstrike.config.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ConfigData(String mode) implements CommonPacket
{
    public static final Type<ConfigData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "config_data"));

    public static final StreamCodec<FriendlyByteBuf, ConfigData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ConfigData::mode,
        ConfigData::new
    );

    public ConfigData () {
        this(ModConfig.GENERAL.mode.get().toString());
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    @Override
    public void handleMessage (Player player, Consumer<Runnable> workQueue) {
        workQueue.accept(() -> {
            ModConfig.GENERAL.mode.set(ModConfig.Mode.valueOf(mode));
        });
    }
}
