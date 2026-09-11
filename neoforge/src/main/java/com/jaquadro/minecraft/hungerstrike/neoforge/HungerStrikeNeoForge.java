package com.jaquadro.minecraft.hungerstrike.neoforge;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.command.HungerStrikeCommand;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * NeoForge entrypoint. Installs the platform bridge and wires the shared code in {@code common/}
 * to NeoForge's event buses.
 */
@Mod(HungerStrike.MOD_ID)
public final class HungerStrikeNeoForge {
    public HungerStrikeNeoForge(IEventBus modBus, ModContainer modContainer) {
        HungerStrike.init(new NeoForgePlatform());

        HungerStrikeAttachments.ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(NeoForgeNetwork::register);
        modBus.addListener(this::modifyFoodStackSize);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new HungerStrikeEvents());
    }

    /**
     * Applies the {@code maxFoodStackSize} override. On 1.20.5+ the stack size is a data component
     * rather than a field on {@link net.minecraft.world.item.Item}, so this replaces the access
     * transformer the Forge builds used.
     */
    private void modifyFoodStackSize(ModifyDefaultComponentsEvent event) {
        int stackSize = Config.maxFoodStackSize();
        if (stackSize <= 0) {
            return;
        }

        event.modifyMatching(
                (item, components) -> components.get(DataComponents.FOOD) != null,
                (components, context, item) -> components.set(DataComponents.MAX_STACK_SIZE, stackSize));
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HungerStrikeCommand.register(event.getDispatcher());
    }

    /**
     * The per-player strike flag rides along on the data attachment's own sync. The active mode is
     * server state, so it has to be pushed to the client explicitly on join.
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NeoForgeNetwork.sendModeTo(player);
        }
    }
}
