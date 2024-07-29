package com.jaquadro.minecraft.hungerstrike;

import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;

public class ExtendedPlayerHandler
{
    public static Capability<ExtendedPlayer> EXTENDED_PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register (RegisterCapabilitiesEvent event) {
        event.register(ExtendedPlayer.class);
    }
}
