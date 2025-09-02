package com.jaquadro.minecraft.hungerstrike.proxy;

import com.jaquadro.minecraft.hungerstrike.ExtendedPlayer;
import com.jaquadro.minecraft.hungerstrike.ExtendedPlayerProvider;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.PlayerHandler;
import com.jaquadro.minecraft.hungerstrike.network.RequestSyncMessage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.jaquadro.minecraft.hungerstrike.HungerStrike.proxy;

public class CommonProxy
{

    public PlayerHandler playerHandler;

    public CommonProxy () {
        playerHandler = new PlayerHandler();
    }

    public void registerNetworkHandlers () {
        HungerStrike.network.registerMessage(RequestSyncMessage.Handler.class, RequestSyncMessage.class, RequestSyncMessage.MESSAGE_ID, Side.SERVER);
    }

    @SubscribeEvent
    public void tick (TickEvent.PlayerTickEvent event) {
        playerHandler.tick(event.player, event.phase, event.side);
    }

    @SubscribeEvent
    public void attachCapabilites (AttachCapabilitiesEvent event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(ExtendedPlayer.EXTENDED_PLAYER_KEY, new ExtendedPlayerProvider((EntityPlayer) event.getObject()));
    }

    @SubscribeEvent
    public void livingDeath (LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getEntityWorld().isRemote && entity instanceof EntityPlayerMP)
            playerHandler.storeData((EntityPlayer) entity);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!getEffectiveHungerStrike(player)) return;
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ItemFood) {
            if (!player.canEat(false) && player.getHealth() < player.getMaxHealth()) {
                player.setActiveHand(event.getHand());
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!getEffectiveHungerStrike(player)) return;
        BlockPos pos = event.getPos();
        Block block = event.getWorld().getBlockState(pos).getBlock();
        if (block instanceof BlockCake cake && player.getHealth() < player.getMaxHealth()) {
            player.heal(1);
        }
    }

    public boolean getEffectiveHungerStrike (EntityPlayer player) {
        return switch (HungerStrike.config.getMode()) {
            case NONE -> false;
            case ALL -> true;
            case LIST -> proxy.playerHandler.isOnHungerStrike(Minecraft.getMinecraft().player);
        };
    }

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getEntityWorld().isRemote && entity instanceof EntityPlayerMP)
            playerHandler.restoreData((EntityPlayer) entity);
        else if (entity.getEntityWorld().isRemote && entity instanceof EntityPlayerSP)
            HungerStrike.network.sendToServer(new RequestSyncMessage());
    }
}
