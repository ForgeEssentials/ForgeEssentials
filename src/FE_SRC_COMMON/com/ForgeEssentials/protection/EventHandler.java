package com.ForgeEssentials.protection;

import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.ForgeEssentials.core.customEvents.PlayerBlockBreak;
import com.ForgeEssentials.core.customEvents.PlayerBlockPlace;
import com.ForgeEssentials.permission.APIHelper;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.permission.ZoneManager;
import com.ForgeEssentials.permission.query.PermQuery;
import com.ForgeEssentials.permission.query.PermQueryPlayerZone;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class EventHandler
{
	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void breakEvent(PlayerBlockBreak e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		WorldPoint point = new WorldPoint(e.player.dimension, e.blockX, e.blockY, e.blockZ);
		Zone zone = ZoneManager.getWhichZoneIn(point);
		PermQuery query = new PermQueryPlayerZone(e.player, ModuleProtection.PERM_OVERRIDE, zone);
		Boolean result = APIHelper.checkPermAllowed(query);

		if (!result)
		{
			query = new PermQueryPlayerZone(e.player, ModuleProtection.PERM_EDITS, zone);
			result = APIHelper.checkPermAllowed(query);
		}

		e.setCanceled(!result);
	}

	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void placeEvent(PlayerBlockPlace e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		WorldPoint point = new WorldPoint(e.player.dimension, e.blockX, e.blockY, e.blockZ);
		Zone zone = ZoneManager.getWhichZoneIn(point);
		PermQuery query = new PermQueryPlayerZone(e.player, ModuleProtection.PERM_OVERRIDE, zone);
		Boolean result = APIHelper.checkPermAllowed(query);

		if (!result)
		{
			query = new PermQueryPlayerZone(e.player, ModuleProtection.PERM_EDITS, zone);
			result = APIHelper.checkPermAllowed(query);
		}

		e.setCanceled(!result);
	}

	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			WorldPoint point = new WorldPoint(e.entityPlayer.dimension, e.x, e.y, e.z);
			Zone zone = ZoneManager.getWhichZoneIn(point);
			PermQuery query = new PermQueryPlayerZone(e.entityPlayer, ModuleProtection.PERM_OVERRIDE, zone);
			Boolean result = APIHelper.checkPermAllowed(query);

			if (!result)
			{
				query = new PermQueryPlayerZone(e.entityPlayer, ModuleProtection.PERM_INTERACT_BLOCK, zone);
				result = APIHelper.checkPermAllowed(query);
			}

			e.setCanceled(!result);
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGH)
	public void entityInteractEvent(EntityInteractEvent e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		WorldPoint point = new WorldPoint(e.entityPlayer.dimension, (int) e.target.posX, (int) e.target.posY, (int) e.target.posZ);
		Zone zone = ZoneManager.getWhichZoneIn(point);

		PermQuery query = new PermQueryPlayerZone(e.entityPlayer, ModuleProtection.PERM_OVERRIDE, zone);
		Boolean result = APIHelper.checkPermAllowed(query);

		if (!result)
		{
			query = new PermQueryPlayerZone(e.entityPlayer, ModuleProtection.PERM_INTERACT_ENTITY, zone);
			result = APIHelper.checkPermAllowed(query);
		}

		OutputHandler.debug("entityInteractEvent in zone: " + zone.getZoneName() + " result: " + result);

		e.setCanceled(!result);
	}
}
