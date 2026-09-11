package com.jaquadro.minecraft.hungerstrike.neoforge;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.HungerStrikeState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-only entrypoint. Hides the food bar for players the mod is active on, which replaces the
 * {@code ClientProxy} of the Forge builds.
 */
@Mod(value = HungerStrike.MOD_ID, dist = Dist.CLIENT)
public final class HungerStrikeNeoForgeClient {
    public HungerStrikeNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(this::onRenderGuiLayer);
        NeoForge.EVENT_BUS.addListener(this::onLoggingOut);
    }

    /** Without this a mode pushed by one server would linger into the next world joined. */
    private void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        Config.clearServerMode();
    }

    private void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.FOOD_LEVEL.equals(event.getName()) || !Config.hideHungerBar()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && HungerStrikeState.isActiveFor(player)) {
            event.setCanceled(true);
        }
    }
}
