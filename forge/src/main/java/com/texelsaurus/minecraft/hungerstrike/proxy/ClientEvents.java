package com.texelsaurus.minecraft.hungerstrike.proxy;

// Forge removed GUI layer event even though Minecraft has not broken up GUI elements into separate layers
// This can only be reintroduced with a mixin

/*
import com.texelsaurus.minecraft.hungerstrike.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientEvents
{
    CommonEvents common;

    public ClientEvents (CommonEvents common) {
        this.common = common;

        MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlay);
    }

    private void renderGameOverlay (RenderGuiOverlayEvent.Pre event) {

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
*/