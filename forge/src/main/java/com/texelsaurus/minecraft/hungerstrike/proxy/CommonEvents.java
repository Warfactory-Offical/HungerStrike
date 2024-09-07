package com.texelsaurus.minecraft.hungerstrike.proxy;

import com.texelsaurus.minecraft.hungerstrike.ExtendedPlayerProvider;
import com.texelsaurus.minecraft.hungerstrike.ForgeExtendedPlayer;
import com.texelsaurus.minecraft.hungerstrike.ModServices;
import com.texelsaurus.minecraft.hungerstrike.PlayerHandler;
import com.texelsaurus.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.texelsaurus.minecraft.hungerstrike.network.SyncRequest;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEvents
{
    public PlayerHandler playerHandler;

    public CommonEvents () {
        playerHandler = new PlayerHandler();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tick (TickEvent.PlayerTickEvent.Pre event) {
        Player player = event.player;
        playerHandler.tickStart(player);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent.Post event) {
        Player player = event.player;
        playerHandler.tickEnd(player);
    }

    @SubscribeEvent
    public void attachCapabilites (AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof Player)
            event.addCapability(ForgeExtendedPlayer.EXTENDED_PLAYER_KEY, new ExtendedPlayerProvider((Player) event.getObject()));
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
           ModServices.NETWORK.sendToServer(new SyncRequest());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HungerStrikeCommand.register(event.getDispatcher());
    }
}
