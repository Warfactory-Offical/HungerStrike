package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.network.NetworkHandler;
import com.jaquadro.minecraft.hungerstrike.proxy.ClientEvents;
import com.jaquadro.minecraft.hungerstrike.proxy.CommonEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

        ModLoadingContext.get().registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ModConfig.spec);
        modEventBus.addListener(this::setup);

        Attachments.register(modEventBus);

        modEventBus.addListener(NetworkHandler::register);
    }

    private void setup (final FMLCommonSetupEvent event) {
        Registry<Item> itemRegistry = BuiltInRegistries.ITEM;
        if (ModConfig.GENERAL.foodStackSize.get() > -1) {
            for (Item item : itemRegistry) {
                if (item != null && item.isEdible())
                    item.maxStackSize = ModConfig.GENERAL.foodStackSize.get();
            }
        }
    }
}
