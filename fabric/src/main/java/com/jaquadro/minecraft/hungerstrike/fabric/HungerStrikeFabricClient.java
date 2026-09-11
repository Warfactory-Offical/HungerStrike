package com.jaquadro.minecraft.hungerstrike.fabric;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrikeState;
import com.jaquadro.minecraft.hungerstrike.network.SyncModePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client-only entrypoint. Hides the food bar for players the mod is active on.
 *
 * <p>Where the NeoForge module cancels {@code RenderGuiLayerEvent.Pre} for
 * {@code VanillaGuiLayers.FOOD_LEVEL}, Fabric wraps the registered HUD element and skips the
 * delegate — the same effect, and it leaves every other mod's decoration of that element intact.
 */
public final class HungerStrikeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncModePayload.TYPE,
                (payload, context) -> Config.setModeFromServer(payload.mode()));

        // Without this a mode pushed by one server would linger into the next world joined.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Config.clearServerMode());

        HudElementRegistry.replaceElement(VanillaHudElements.FOOD_BAR,
                original -> (graphics, deltaTracker) -> {
                    if (!shouldHideHungerBar()) {
                        original.extractRenderState(graphics, deltaTracker);
                    }
                });
    }

    private static boolean shouldHideHungerBar() {
        if (!Config.hideHungerBar()) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && HungerStrikeState.isActiveFor(player);
    }
}
