package com.texelsaurus.minecraft.hungerstrike.proxy;

import com.texelsaurus.minecraft.hungerstrike.PlayerHandler;
import com.texelsaurus.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.texelsaurus.minecraft.hungerstrike.network.NetworkHandler;
import com.texelsaurus.minecraft.hungerstrike.network.SyncRequest;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class CommonEvents
{

    public PlayerHandler playerHandler;

    public CommonEvents () {
        playerHandler = new PlayerHandler();

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tick (PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        playerHandler.tickStart(event.getEntity(), player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT);
    }

    @SubscribeEvent
    public void tick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        playerHandler.tickEnd(event.getEntity(), player instanceof ServerPlayer ? LogicalSide.SERVER : LogicalSide.CLIENT);
    }

    /*@SubscribeEvent
    public void livingDeath (LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getCommandSenderWorld().isClientSide && entity instanceof ServerPlayer)
            playerHandler.storeData((Player) entity);
    }*/

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        //if (!event.getLevel().isClientSide && entity instanceof ServerPlayer)
        //    playerHandler.restoreData((Player) entity);
        if (event.getLevel().isClientSide && entity instanceof Player)
           NetworkHandler.sendToServer(new SyncRequest());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HungerStrikeCommand.register(event.getDispatcher());
    }
}
