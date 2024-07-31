package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ConfigData(String mode) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(HungerStrike.MOD_ID, "config_data");

    public ConfigData() {
        this(ModConfig.GENERAL.mode.get().toString());
    }

    public ConfigData(final FriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    @Override
    public void write (FriendlyByteBuf buffer) {
        buffer.writeUtf(mode);
    }

    @Override
    public ResourceLocation id () {
        return ID;
    }

    public static void handleClient(final ConfigData data, final PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            ModConfig.GENERAL.mode.set(ModConfig.Mode.valueOf(data.mode()));
        }).exceptionally(e -> null);
    }
}
