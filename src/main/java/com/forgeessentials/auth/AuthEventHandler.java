package com.forgeessentials.auth;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class AuthEventHandler
{

    public static String banned;
    public static String notvip;
    public static int vipslots;
    public static int offset;
    public int counter;
    public int maxcounter;

    public AuthEventHandler()
    {
        LoggingHandler.felog.info("FEauth initialized. Enabled: " + ModuleAuth.isEnabled());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;

        if (event.before.getX() == event.after.getX() && event.before.getZ() == event.after.getZ())
        {
            return;
        }
        if (ModuleAuth.canMoveWithoutLogin)
        {
            return;
        }
        if (!ModuleAuth.hasSession.contains(event.entityPlayer.getPersistentID()) || event.entityPlayer instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ServerChatEvent event)
    {
        UUID username = event.player.getPersistentID();
        if (!ModuleAuth.hasSession.contains(username) || event.player instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(CommandEvent event)
    {
        if (!(event.sender instanceof EntityPlayerMP))
            return;

        if (!(event.sender instanceof EntityPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.sender;
        if (player instanceof FakePlayer)
        {
            return;
        }
        if (!ModuleAuth.hasSession.contains(player.getPersistentID()) && !(event.command instanceof CommandAuth))
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(player, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;

        if (!ModuleAuth.hasSession.contains(event.entityPlayer.getPersistentID()) ||  event.entityPlayer instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(EntityInteractEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;

        if (!ModuleAuth.hasSession.contains(event.entityPlayer.getPersistentID()) ||  event.entityPlayer instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(MinecartInteractEvent event)
    {
        if (!(event.player instanceof EntityPlayerMP))
            return;

        if (!ModuleAuth.hasSession.contains(event.player.getPersistentID()) ||  event.player instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTossItem(ItemTossEvent event)
    {
        if (!(event.player instanceof EntityPlayerMP))
            return;

        boolean cancel = false;
        if (!ModuleAuth.hasSession.contains(event.player.getPersistentID()) ||  event.player instanceof FakePlayer)
        {
            cancel = true;
            ChatOutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }

        if (cancel)
        {
            // add the item back to the inventory
            ItemStack stack = event.entityItem.getEntityItem();
            event.player.inventory.addItemStackToInventory(stack);
            event.setCanceled(cancel);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(EntityItemPickupEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;

        if (!ModuleAuth.hasSession.contains(event.entityPlayer.getPersistentID()) ||  event.entityPlayer instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayerMP))
            return;

        EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
        if (!ModuleAuth.hasSession.contains(player.getPersistentID()) ||  player instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(player, "Login required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(AttackEntityEvent event)
    {
        if (!(event.entityPlayer instanceof EntityPlayerMP))
            return;

        if (!ModuleAuth.hasSession.contains(event.entityPlayer.getPersistentID()) ||  event.entityPlayer instanceof FakePlayer)
        {
            event.setCanceled(true);
            ChatOutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }
    }

    /*
     * @SubscribeEvent(priority = EventPriority.HIGHEST) public void onPlayerOpenContainer(PlayerOpenContainerEvent event) { UUID username = event.entityPlayer.getPersistentID();
     * 
     * if (!ModuleAuth.hasSession.contains(username)) { event.setResult(Result.DENY); ChatOutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help."); } }
     */

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (!ModuleAuth.isEnabled())
        {
            return;
        }
        if (!PlayerPassData.isRegistered(e.player.getPersistentID()))
        {
            ChatOutputHandler.chatError(e.player, "Registration required. Try /auth help.");
        }
        else
        {
            ChatOutputHandler.chatError(e.player, "Login required. Try /auth help.");
        }

        maxcounter = FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers() - vipslots - offset;
        if (PermissionManager.checkPermission(e.player, "fe.auth.isVIP"))
        {
            return;
        }
        else if (counter == maxcounter)
        {
            ((EntityPlayerMP) e.player).playerNetServerHandler.kickPlayerFromServer(notvip);
        }
        else
        {
            counter = counter + 1;
        }
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e)
    {
        ModuleAuth.hasSession.remove(e.player.getPersistentID());
        PlayerPassData.removeFromCache(e.player.getPersistentID());
        counter = counter - 1;
    }

}
