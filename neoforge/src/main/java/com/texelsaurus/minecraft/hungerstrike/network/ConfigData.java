package com.texelsaurus.minecraft.hungerstrike.network;

import com.texelsaurus.minecraft.hungerstrike.HungerStrike;
import com.texelsaurus.minecraft.hungerstrike.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConfigData(String mode) implements CustomPacketPayload
{
    public static final Type<ConfigData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HungerStrike.MOD_ID, "config_data"));

    public static final StreamCodec<FriendlyByteBuf, ConfigData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        ConfigData::mode,
        ConfigData::new
    );

    public ConfigData() {
        this(ModConfig.GENERAL.mode.get().toString());
    }

    @Override
    public Type<? extends CustomPacketPayload> type () {
        return TYPE;
    }

    public static void handleClient(final ConfigData data, final IPayloadContext context) {
        ModConfig.GENERAL.mode.set(ModConfig.Mode.valueOf(data.mode()));
    }
}
