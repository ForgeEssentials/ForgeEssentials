package com.ForgeEssentials.util.events;

import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ForgeEssentialsEventFactory implements ITickHandler, IPlayerTracker
{
	// TICK STUFF

	private HashMap<String, WarpPoint>	befores;

	public ForgeEssentialsEventFactory()
	{
		befores = new HashMap<String, WarpPoint>();
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		EntityPlayerMP player = (EntityPlayerMP) tickData[0];

		WarpPoint before = befores.get(player.username);
		WarpPoint current = new WarpPoint(player);

		// obviously.. if there IS no before.. don't worry about it.
		if (before == null)
		{
			befores.put(player.username, current);
			return;
		}

		// no respawn stuff or respawn stuff
		if (player.isDead || player.worldObj == null || before.dim != current.dim)
		{
			befores.remove(player.username);
			return;
		}

		if (before.equals(current))
			return;

		PlayerMoveEvent event = new PlayerMoveEvent(player, before, current);
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled())
		{
			FunctionHelper.setPlayer(player, before);
		}
		else
		{
			befores.put(player.username, current);
		}
	}

	// PLAYER TRACKER STUFF

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		befores.remove(player.username);
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel()
	{
		return "PlayerMoveHandler";
	}

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
	}

	// ZONE STUFF
	@ForgeSubscribe
	public void playerMove(PlayerMoveEvent e)
	{
		Zone before = ZoneManager.getWhichZoneIn(e.before);
		Zone after = ZoneManager.getWhichZoneIn(e.after);

		if (before != after)
		{
			PlayerChangedZone event = new PlayerChangedZone(e.entityPlayer, before, after, e.before, e.after);
			MinecraftForge.EVENT_BUS.post(event);
			e.setCanceled(event.isCanceled());
		}
	}

	// BLOCK STUFF

	public static boolean onBlockHarvested(World world, int x, int y, int z, Block block, int metadata, EntityPlayer player)
	{
		PlayerBlockBreak ev = new PlayerBlockBreak(world, x, y, z, player);
		MinecraftForge.EVENT_BUS.post(ev);
		return !ev.isCanceled();
	}

	public static boolean onBlockPlace(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz)
	{
	    // calculate offsets.
	    ForgeDirection dir = ForgeDirection.getOrientation(side);
	    x += dir.offsetX;
	    y += dir.offsetY;
	    z += dir.offsetZ;
	    
		PlayerBlockPlace ev = new PlayerBlockPlace(itemStack, player, world, x, y, z, side, hitx, hity, hitz);
		MinecraftForge.EVENT_BUS.post(ev);
		return !ev.isCanceled();
	}
}
