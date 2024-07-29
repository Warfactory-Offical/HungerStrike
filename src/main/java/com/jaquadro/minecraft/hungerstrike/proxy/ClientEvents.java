package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ModConfig;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.GuiOverlayManager;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents
{
    CommonEvents common;

    public ClientEvents (CommonEvents common) {
        this.common = common;

        NeoForge.EVENT_BUS.addListener(this::renderGameOverlay);
    }

    private void renderGameOverlay (RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == GuiOverlayManager.findOverlay(VanillaGuiOverlay.FOOD_LEVEL.id())) {
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
