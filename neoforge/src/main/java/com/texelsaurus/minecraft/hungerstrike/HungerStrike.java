package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.proxy.ClientEvents;
import com.texelsaurus.minecraft.hungerstrike.proxy.CommonEvents;
import com.texelsaurus.minecraft.hungerstrike.service.NeoforgeConfig;
import com.texelsaurus.minecraft.hungerstrike.service.NeoforgeNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(HungerStrike.MOD_ID)
public class HungerStrike
{
    public static final String MOD_ID = "hungerstrike";

    public static CommonEvents commonEvents;
    public static ClientEvents clientEvents;

    public HungerStrike(ModContainer modContainer, IEventBus modEventBus) {
        commonEvents = new CommonEvents();
        if (FMLEnvironment.dist.isClient())
            clientEvents = new ClientEvents(commonEvents);

        NeoforgeConfig.init();
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, NeoforgeConfig.spec);

        NeoforgeNetworking.init(modEventBus);
        Attachments.register(modEventBus);
    }
}
