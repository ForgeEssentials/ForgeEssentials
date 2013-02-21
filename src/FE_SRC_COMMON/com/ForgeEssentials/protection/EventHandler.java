package com.ForgeEssentials.protection;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQuery;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerZone;
import com.ForgeEssentials.core.customEvents.PlayerBlockBreak;
import com.ForgeEssentials.core.customEvents.PlayerBlockPlace;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class EventHandler
{
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void playerAttack(AttackEntityEvent e)
	{
		if (e.target instanceof EntityPlayer)
		{
			// stopping pvp damage
			EntityPlayer receiver = (EntityPlayer) e.target;

			boolean sourceB = !PermissionsAPI.checkPermAllowed(new PermQueryPlayer(e.entityPlayer, ModuleProtection.PERM_PVP));
			boolean receiverB = !PermissionsAPI.checkPermAllowed(new PermQueryPlayer(e.entityPlayer, ModuleProtection.PERM_PVP));

			if (sourceB || receiverB)
			{
				e.setCanceled(true);
			}
		}
		else
		{
			// stopping entity damage

			PermQuery query = new PermQueryPlayer(e.entityPlayer, ModuleProtection.PERM_OVERRIDE);
			Boolean result = PermissionsAPI.checkPermAllowed(query);

			if (!result)
			{
				query = new PermQueryPlayer(e.entityPlayer, ModuleProtection.PERM_INTERACT_ENTITY);
				result = PermissionsAPI.checkPermAllowed(query);
			}

			e.setCanceled(!result);
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void breakEvent(PlayerBlockBreak e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		WorldPoint point = new WorldPoint(e.player.dimension, e.blockX, e.blockY, e.blockZ);
		PermQuery query = new PermQueryPlayerArea(e.player, ModuleProtection.PERM_OVERRIDE, point);
		Boolean result = PermissionsAPI.checkPermAllowed(query);

		if (!result)
		{
			query = new PermQueryPlayerArea(e.player, ModuleProtection.PERM_EDITS, point);
			result = PermissionsAPI.checkPermAllowed(query);
		}

		e.setCanceled(!result);
	}

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void placeEvent(PlayerBlockPlace e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		WorldPoint point = new WorldPoint(e.player.dimension, e.blockX, e.blockY, e.blockZ);
		PermQuery query = new PermQueryPlayerArea(e.player, ModuleProtection.PERM_OVERRIDE, point);
		Boolean result = PermissionsAPI.checkPermAllowed(query);

		if (!result)
		{
			query = new PermQueryPlayerArea(e.player, ModuleProtection.PERM_EDITS, point);
			result = PermissionsAPI.checkPermAllowed(query);
		}

		e.setCanceled(!result);
	}

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			WorldPoint point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);
			PermQuery query = new PermQueryPlayerArea(e.entityPlayer, ModuleProtection.PERM_OVERRIDE, point);
			Boolean result = PermissionsAPI.checkPermAllowed(query);

			if (!result)
			{
				query = new PermQueryPlayerArea(e.entityPlayer, ModuleProtection.PERM_INTERACT_BLOCK, point);
				result = PermissionsAPI.checkPermAllowed(query);
			}

			e.setCanceled(!result);
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void entityInteractEvent(EntityInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		WorldPoint point = new WorldPoint(e.entityPlayer.dimension, (int) e.target.posX, (int) e.target.posY, (int) e.target.posZ);
		Zone zone = ZoneManager.getWhichZoneIn(point);

		PermQuery query = new PermQueryPlayerZone(e.entityPlayer, ModuleProtection.PERM_OVERRIDE, zone);
		Boolean result = PermissionsAPI.checkPermAllowed(query);

		if (!result)
		{
			query = new PermQueryPlayerZone(e.entityPlayer, ModuleProtection.PERM_INTERACT_ENTITY, zone);
			result = PermissionsAPI.checkPermAllowed(query);
		}

		OutputHandler.finest("entityInteractEvent in zone: " + zone.getZoneName() + " result: " + result);

		e.setCanceled(!result);
	}
}
