package com.jaquadro.minecraft.hungerstrike;

import com.jaquadro.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.jaquadro.minecraft.hungerstrike.network.HungerStrikeNetwork;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hunger Strike restores pre-1.8 (pre-hunger) health mechanics: the hunger bar is pinned at a
 * configurable baseline and eating food heals the player directly instead of filling the bar.
 */
@Mod(HungerStrike.MOD_ID)
public final class HungerStrike {
    public static final String MOD_ID = "hungerstrike";
    public static final Logger LOGGER = LoggerFactory.getLogger("Hunger Strike");

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public HungerStrike(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        HungerStrikeAttachments.ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(HungerStrikeNetwork::register);
        modBus.addListener(this::modifyFoodStackSize);
        modBus.addListener(this::onConfigLoad);
        modBus.addListener(this::onConfigReload);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new HungerStrikeHandler());
    }

    private void onConfigLoad(ModConfigEvent.Loading event) {
        refreshConfig(event.getConfig());
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        refreshConfig(event.getConfig());
    }

    private void refreshConfig(ModConfig config) {
        if (config.getSpec() == Config.SPEC) {
            Config.refreshFromSpec();
        }
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
            HungerStrikeNetwork.sendModeTo(player);
        }
    }
}
