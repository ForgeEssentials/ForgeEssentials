package com.ForgeEssentials.core;

import com.ForgeEssentials.core.customEvents.PlayerBlockBreak;
import com.ForgeEssentials.core.customEvents.PlayerBlockPlace;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;

public class CustomEventFactory
{
	public static boolean onBlockHarvested(World world, int x, int y, int z, Block block, int metadata, EntityPlayer player)
	{
		PlayerBlockBreak ev = new PlayerBlockBreak(world, x, y, z, player);
		MinecraftForge.EVENT_BUS.post(ev);
		return !ev.isCanceled();
	}

	public static boolean onBlockPlace(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz)
	{
		PlayerBlockPlace ev = new PlayerBlockPlace(itemStack, player, world, x, y, z, side, hitx, hity, hitz);
		MinecraftForge.EVENT_BUS.post(ev);
		return !ev.isCanceled();
	}
}
