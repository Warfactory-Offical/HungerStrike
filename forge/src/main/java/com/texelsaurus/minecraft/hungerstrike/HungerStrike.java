package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.service.ForgeConfig;
import com.texelsaurus.minecraft.hungerstrike.service.ForgeNetworking;
import com.texelsaurus.minecraft.hungerstrike.proxy.CommonEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(HungerStrike.MOD_ID)
public class HungerStrike
{
    public static final String MOD_ID = "hungerstrike";

    public static CommonEvents commonEvents;
    //public static ClientEvents clientEvents;

    public HungerStrike() {
        commonEvents = new CommonEvents();
        //if (FMLEnvironment.dist.isClient())
        //    clientEvents = new ClientEvents(commonEvents);

        ForgeConfig.init();
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ForgeConfig.spec);

        ForgeNetworking.init();
    }

    /*
    @SubscribeEvent
    public void registerCapabilities (RegisterCapabilitiesEvent event) {
        ExtendedPlayerHandler.register(event);
    }*/
}
