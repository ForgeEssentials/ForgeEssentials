package com.ForgeEssentials.permission;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.EventPriority;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.permission.query.PermQueryPlayerArea;
import com.ForgeEssentials.permission.query.PermQueryPlayerZone;
import com.ForgeEssentials.permission.query.PermSubscribe;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;

/**
 * 
 * This is the default catcher of all the ForgeEssentials Permission checks.
 * Mods can inherit from any of the ForgeEssentials Permissions and specify more specific
 * catchers to get first crack at handling them.
 * 
 * The handling performed here is limited to basic area permission checks, and is not aware
 * of anything else other mods add to the system.
 * 
 * @author AbrarSyed
 * 
 */
public final class PermissionsHandler
{
	@PermSubscribe(priority = EventPriority.NORMAL)
	public void handlerQuery(PermQueryPlayer event)
	{
		Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(event.doer), event.doer.worldObj);
		PermResult result = getResultFromZone(zone, event.checker, event.doer);
		event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL)
	public void handlerQuery(PermQueryPlayerZone event)
	{
		PermResult result = getResultFromZone(event.toCheck, event.checker, event.doer);
		event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL)
	public void handlerQuery(PermQueryPlayerArea event)
	{
		if (event.allOrNothing)
		{
			Zone zone = ZoneManager.getWhichZoneIn(event.doneTo, event.doer.worldObj);
			PermResult result = getResultFromZone(zone, event.checker, event.doer);
			event.setResult(result);
		}
		else
		{
			event.applicable = getApplicableZones(event.checker, event.doer, event.doneTo);
			if (event.applicable == null)
				event.setResult(PermResult.DENY);
			else if (event.applicable.isEmpty())
				event.setResult(PermResult.ALLOW);
			else
				event.setResult(PermResult.PARTIAL);
		}
	}

	/**
	 * 
	 * @param zone Zone to check permissions in.
	 * @param perm The permission to check.
	 * @param player Player to check/
	 * @return the result for the perm.
	 */
	private PermResult getResultFromZone(Zone zone, PermissionChecker perm, EntityPlayer player)
	{
		PermResult result = PermResult.UNKNOWN;
		Zone tempZone = zone;
		while (result.equals(PermResult.UNKNOWN))
		{
			String group = "_DEFAULT_";
			result = tempZone.getPlayerOverride(player, perm);

			if (result.equals(PermResult.UNKNOWN)) // or group blankets
				result = tempZone.getGroupOverride(group, perm);

			if (result.equals(PermResult.UNKNOWN))
				if (tempZone == ZoneManager.GLOBAL)
					result = Permission.getPermissionDefault(perm.name);
				else
					tempZone = ZoneManager.getZone(tempZone.parent);
		}
		return result;
	}

	private ArrayList<AreaBase> getApplicableZones(PermissionChecker perm, EntityPlayer player, AreaBase doneTo)
	{
		PlayerInfo.getPlayerInfo(player);
		ArrayList<AreaBase> applicable = new ArrayList<AreaBase>();

		Zone worldZone = ZoneManager.getWorldZone(player.worldObj);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all children
		for (Zone zone : ZoneManager.zoneMap.values())
			if (zone.intersectsWith(doneTo) && worldZone.isParentOf(zone))
				zones.add(zone);

		switch (zones.size())
			{
			// no children of the world? return the worldZone
				case 0:
					{
						PermResult result = getResultFromZone(worldZone, perm, player);
						if (result.equals(PermResult.ALLOW))
							return applicable;
						else
							return null;
					}
				// only 1 usable Zone? use it.
				case 1:
					{
						PermResult result = getResultFromZone(zones.get(0), perm, player);
						if (result.equals(PermResult.ALLOW))
							return applicable;
						else
							return null;
					}
				// else.. get the applicable states.
				default:
					{
						for (Zone zone : zones)
							if (getResultFromZone(zone, perm, player).equals(PermResult.ALLOW))
								applicable.add(doneTo.getIntersection(zone));
					}
			}

		return applicable;
	}
}
