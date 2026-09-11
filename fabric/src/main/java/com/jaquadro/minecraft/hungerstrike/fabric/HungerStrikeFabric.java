package com.jaquadro.minecraft.hungerstrike.fabric;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.jaquadro.minecraft.hungerstrike.network.SyncModePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.component.DataComponents;

/**
 * Fabric entrypoint, running on both sides. Installs the platform bridge and wires the shared code
 * in {@code common/} to Fabric API's events. The client-only half lives in
 * {@link HungerStrikeFabricClient}.
 */
public final class HungerStrikeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HungerStrike.init(new FabricPlatform());

        HungerStrikeAttachments.register();
        // Both sides must know the payload type: the server to send it, the client to receive it.
        PayloadTypeRegistry.clientboundPlay().register(SyncModePayload.TYPE, SyncModePayload.STREAM_CODEC);

        DefaultItemComponentEvents.MODIFY.register(HungerStrikeFabric::modifyFoodStackSize);
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> HungerStrikeCommand.register(dispatcher));

        // The per-player strike flag rides along on the data attachment's own sync. The active mode
        // is server state, so it has to be pushed to the client explicitly on join.
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, server) -> HungerStrike.platform().sendModeTo(handler.player));
    }

    /**
     * Applies the {@code maxFoodStackSize} override. On 1.20.5+ the stack size is a data component
     * rather than a field on {@link net.minecraft.world.item.Item}, so this replaces the access
     * transformer the Forge builds used, the same way NeoForge's
     * {@code ModifyDefaultComponentsEvent} does.
     */
    private static void modifyFoodStackSize(DefaultItemComponentEvents.ModifyContext context) {
        int stackSize = Config.maxFoodStackSize();
        if (stackSize <= 0) {
            return;
        }

        context.modify(
                item -> item.components().get(DataComponents.FOOD) != null,
                (builder, lookupProvider, item) -> builder.set(DataComponents.MAX_STACK_SIZE, stackSize));
    }
}
