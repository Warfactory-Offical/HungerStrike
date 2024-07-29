package com.jaquadro.minecraft.hungerstrike.network;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.NetworkEvent;

public class PacketSyncConfig
{
    private String mode;

    public PacketSyncConfig() {
        this(ModConfig.GENERAL.mode.get().toString());
    }

    public PacketSyncConfig(String mode) {
        this.mode = mode;
    }

    public PacketSyncConfig(FriendlyByteBuf buf) {
        this.mode = buf.readUtf(128);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(mode, 128);
    }

    public void handle(NetworkEvent.Context ctx) {
        if (FMLEnvironment.dist.isClient())
            handle(this);

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handle(PacketSyncConfig message) {
        ModConfig.GENERAL.mode.set(ModConfig.Mode.valueOf(message.mode));
    }
}
