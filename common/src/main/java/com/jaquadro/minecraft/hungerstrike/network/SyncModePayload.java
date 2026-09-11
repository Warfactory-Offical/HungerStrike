package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Tells the client which mode the server is running in, so it can decide whether to hide the hunger
 * bar. Sent on join and whenever an operator changes the mode with {@code /hungerstrike setmode}.
 */
public record SyncModePayload(Config.Mode mode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncModePayload> TYPE =
            new CustomPacketPayload.Type<>(HungerStrike.id("sync_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncModePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.idMapper(SyncModePayload::modeById, Config.Mode::ordinal),
                    SyncModePayload::mode,
                    SyncModePayload::new);

    public static SyncModePayload current() {
        return new SyncModePayload(Config.mode());
    }

    private static Config.Mode modeById(int id) {
        Config.Mode[] values = Config.Mode.values();
        if (id < 0 || id >= values.length) {
            throw new IllegalArgumentException("Unknown hunger strike mode id: " + id);
        }
        return values[id];
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
