package com.forgeessentials.auth;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerMoveEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class AuthEventHandler {

    public static String banned;
    public static String notvip;
    public static String notwhitelisted;
    public static boolean whitelist;
    public static int vipslots;
    public static int offset;
    public int counter;
    public int maxcounter;

    public AuthEventHandler()
    {
        OutputHandler.felog.info("FEauth initialized. Enabled: " + ModuleAuth.isEnabled());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        UUID username = event.entityPlayer.getPersistentID();

        if (event.before.xd == event.after.xd && event.before.zd == event.after.zd)
        {
            return;
        }

        if (ModuleAuth.canMoveWithoutLogin)
        {
            return;
        }

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ServerChatEvent event)
    {
        UUID username = event.player.getPersistentID();

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.player, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(CommandEvent event)
    {
        if (!(event.sender instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.sender;

        if (ModuleAuth.unLogged.contains(player.getPersistentID()) && !(event.command instanceof CommandAuth))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(player.getPersistentID()) && !(event.command instanceof CommandAuth))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        UUID username = event.entityPlayer.getPersistentID();

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(EntityInteractEvent event)
    {
        UUID username = event.entityPlayer.getPersistentID();

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(MinecartInteractEvent event)
    {
        UUID username = event.player.getPersistentID();

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.player, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTossItem(ItemTossEvent event)
    {
        UUID username = event.player.getPersistentID();

        boolean cancel = false;

        if (ModuleAuth.unLogged.contains(username))
        {
            cancel = true;

            OutputHandler.chatError(event.player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            cancel = true;
            OutputHandler.chatError(event.player, "Registration required. Try /auth help.");
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
        UUID username = event.entityPlayer.getPersistentID();

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;

        if (ModuleAuth.unLogged.contains(player.getPersistentID()))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(player.getPersistentID()))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(AttackEntityEvent event)
    {
        UUID username = event.entityPlayer.getPersistentID();

        if (ModuleAuth.unLogged.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(event.entityPlayer, "Registration required. Try /auth help.");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (!ModuleAuth.isEnabled())
        {
            return;
        }
        PlayerPassData data = PlayerPassData.getData(e.player.getPersistentID());

        if (data == null)
        {
            OutputHandler.chatError(e.player, "Registration required. Try /auth help.");
            ModuleAuth.unRegistered.add(e.player.getPersistentID());
        }
        else
        {
            OutputHandler.chatError(e.player, "Login required. Try /auth help.");
        }

        maxcounter = FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers() - vipslots - offset;
        if (whitelist)
        {
            if (!PermissionsManager.checkPermission(e.player, "fe.auth.isWhiteListed"))
            {
                ((EntityPlayerMP) e.player).playerNetServerHandler.kickPlayerFromServer(notwhitelisted);
            }
        }
        if (PermissionsManager.checkPermission(e.player, "fe.auth.isVIP"))
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
        ModuleAuth.unLogged.remove(e.player.getPersistentID());
        ModuleAuth.unRegistered.remove(e.player.getPersistentID());
        PlayerPassData.discardData(e.player.getPersistentID());
    }

}
