package com.ForgeEssentials.auth;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.PlayerMoveEvent;

public class EventHandler
{
	public EventHandler()
	{
		// nothing
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		String username = event.entityPlayer.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerChat(ServerChatEvent event)
	{
		String username = event.player.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.player, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.player, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(CommandEvent event)
	{
		if (!(event.sender instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.sender;

		if (ModuleAuth.unLogged.contains(player.username) && !(event.command instanceof CommandAuth))
		{
			event.setCanceled(true);
			OutputHandler.chatError(player, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(player.username) && !(event.command instanceof CommandAuth))
		{
			event.setCanceled(true);
			OutputHandler.chatError(player, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		String username = event.entityPlayer.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(EntityInteractEvent event)
	{
		String username = event.entityPlayer.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(MinecartInteractEvent event)
	{
		String username = event.player.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.player, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.player, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerTossItem(ItemTossEvent event)
	{
		String username = event.player.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.player, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.player, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerTargetted(LivingSetAttackTargetEvent event)
	{
		if (!(event.target instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.target;

		if (ModuleAuth.unLogged.contains(player.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(player, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(player.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(player, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerHurt(LivingHurtEvent event)
	{
		if (!(event.entityLiving instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.entityLiving;

		if (ModuleAuth.unLogged.contains(player.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(player, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(player.username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(player, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(EntityItemPickupEvent event)
	{
		String username = event.entityPlayer.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needregister"));
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerAttack(AttackEntityEvent event)
	{
		String username = event.entityPlayer.username;

		if (ModuleAuth.unLogged.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needlogin"));
		}

		if (ModuleAuth.unRegistered.contains(username))
		{
			event.setCanceled(true);
			OutputHandler.chatError(event.entityPlayer, Localization.get("message.auth.needregister"));
		}
	}

}
