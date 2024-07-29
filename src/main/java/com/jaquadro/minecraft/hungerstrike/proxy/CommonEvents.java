package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayerProvider;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.command.HungerStrikeCommand;
import com.jaquadro.minecraft.hungerstrike.network.PacketHandler;
import com.jaquadro.minecraft.hungerstrike.network.PacketRequestSync;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.minecraft.server.level.ServerPlayer;

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

    @SubscribeEvent
    public void attachCapabilites (AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof Player)
            event.addCapability(ExtendedPlayer.EXTENDED_PLAYER_KEY, new ExtendedPlayerProvider((Player) event.getObject()));
    }

    @SubscribeEvent
    public void livingDeath (LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getCommandSenderWorld().isClientSide && entity instanceof ServerPlayer)
            playerHandler.storeData((Player) entity);
    }

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();

        if (!event.getLevel().isClientSide && entity instanceof ServerPlayer)
            playerHandler.restoreData((Player) entity);
        else if (event.getLevel().isClientSide && entity instanceof Player)
           PacketHandler.INSTANCE.sendToServer(new PacketRequestSync());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        HungerStrikeCommand.register(event.getDispatcher());
    }
}
