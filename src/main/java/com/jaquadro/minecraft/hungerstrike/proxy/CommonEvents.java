package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.jaquadro.minecraft.hungerstrike.network.NetworkHandler;
import com.jaquadro.minecraft.hungerstrike.network.SyncRequest;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

public class CommonEvents
{

    public PlayerHandler playerHandler;

    public CommonEvents () {
        playerHandler = new PlayerHandler();

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tick (TickEvent.PlayerTickEvent event) {
        playerHandler.tick(event.player, event.phase, event.side);
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
