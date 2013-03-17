package com.ForgeEssentials.auth;

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

import com.ForgeEssentials.util.events.PlayerMoveEvent;

public class EventHandler
{

	public EventHandler()
	{
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerChat(ServerChatEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(CommandEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(EntityInteractEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(MinecartInteractEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerTossItem(ItemTossEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerTargetted(LivingSetAttackTargetEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerHurt(LivingHurtEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(EntityItemPickupEvent event)
	{

	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void onPlayerAttack(AttackEntityEvent event)
	{

	}

}
