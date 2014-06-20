package com.forgeessentials.auth;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerMoveEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EventHandler {
    public EventHandler()
    {
        // nothing
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        String username = event.entityPlayer.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ServerChatEvent event)
    {
        String username = event.player.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(CommandEvent event)
    {
        if (!(event.sender instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.sender;

        if (ModuleAuth.unLogged.contains(player.username) && !(event.command instanceof CommandAuth))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(player.username) && !(event.command instanceof CommandAuth))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Registration required. Try /auth help.");
        }
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        String username = event.entityPlayer.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(EntityInteractEvent event)
    {
        String username = event.entityPlayer.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(MinecartInteractEvent event)
    {
        String username = event.player.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerTossItem(ItemTossEvent event)
    {
        String username = event.player.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(EntityItemPickupEvent event)
    {
        String username = event.entityPlayer.username;

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

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;

        if (ModuleAuth.unLogged.contains(player.username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Login required. Try /auth help.");
        }

        if (ModuleAuth.unRegistered.contains(player.username))
        {
            event.setCanceled(true);
            OutputHandler.chatError(player, "Registration required. Try /auth help.");
        }
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(AttackEntityEvent event)
    {
        String username = event.entityPlayer.username;

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

}
