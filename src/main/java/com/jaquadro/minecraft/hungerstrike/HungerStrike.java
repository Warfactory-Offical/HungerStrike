package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.command.CommandHungerStrike;
import com.jaquadro.minecraft.hungerstrike.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION, guiFactory = HungerStrike.SOURCE_PATH + "ModGuiFactory", acceptedMinecraftVersions = "[1.12,1.13)")
public class HungerStrike {
    static final String SOURCE_PATH = "com.jaquadro.minecraft.hungerstrike.";

    @Mod.Instance(Tags.MODID)
    public static HungerStrike instance;

    @SidedProxy(clientSide = SOURCE_PATH + "proxy.ClientProxy", serverSide = SOURCE_PATH + "proxy.ServerProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;
    public static ConfigManager config = new ConfigManager();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(proxy);

        config.setup(event.getSuggestedConfigurationFile());

        ExtendedPlayerHandler.register();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);
        proxy.registerNetworkHandlers();
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ItemFood) {
            if (!player.canEat(false) && player.getHealth() < player.getMaxHealth()) {
                player.setActiveHand(event.getHand());
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (config.getFoodStackSize() > -1) {
            for (Item item : Item.REGISTRY) {
                if (item instanceof ItemFood)
                    item.setMaxStackSize(config.getFoodStackSize());
            }
        }
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHungerStrike());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MODID))
            config.syncConfig();
    }
}
