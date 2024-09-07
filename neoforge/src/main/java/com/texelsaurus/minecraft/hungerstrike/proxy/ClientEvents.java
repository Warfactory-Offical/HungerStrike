package com.texelsaurus.minecraft.hungerstrike.proxy;

import com.texelsaurus.minecraft.hungerstrike.ModConfig;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents
{
    CommonEvents common;

    public ClientEvents (CommonEvents common) {
        this.common = common;

        NeoForge.EVENT_BUS.addListener(this::renderGameOverlay);
    }

    private void renderGameOverlay (RenderGuiLayerEvent.Pre event) {
        if (event.getName() == VanillaGuiLayers.FOOD_LEVEL) {
            if (!ModConfig.GENERAL.hideHungerBar.get())
                return;

            switch (ModConfig.GENERAL.mode.get()) {
                case NONE:
                    break;
                case ALL:
                    event.setCanceled(true);
                    break;
                case LIST:
                    if (common.playerHandler.isOnHungerStrike(Minecraft.getInstance().player))
                        event.setCanceled(true);
                    break;
            }
        }
    }

}
