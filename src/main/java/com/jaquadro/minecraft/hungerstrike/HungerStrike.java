package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.proxy.ClientEvents;
import com.jaquadro.minecraft.hungerstrike.proxy.CommonEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;

@Mod(HungerStrike.MOD_ID)
public class HungerStrike
{
    public static final String MOD_ID = "hungerstrike";

    public static CommonEvents commonEvents;
    public static ClientEvents clientEvents;

    public HungerStrike() {
        commonEvents = new CommonEvents();
        if (FMLEnvironment.dist.isClient())
            clientEvents = new ClientEvents(commonEvents);

        ModLoadingContext.get().registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.spec);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        NeoForge.EVENT_BUS.register(this);
    }

    private void setup (final FMLCommonSetupEvent event) {
        PacketHandler.init();

        Registry<Item> itemRegistry = BuiltInRegistries.ITEM;
        if (ModConfig.GENERAL.foodStackSize.get() > -1) {
            for (Item item : itemRegistry) {
                if (item != null && item.isEdible())
                    item.maxStackSize = ModConfig.GENERAL.foodStackSize.get();
            }
        }
    }

    @SubscribeEvent
    public void registerCapabilities (RegisterCapabilitiesEvent event) {
        ExtendedPlayerHandler.register(event);
    }
}
