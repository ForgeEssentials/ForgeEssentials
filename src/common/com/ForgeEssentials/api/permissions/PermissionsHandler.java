package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.core.PlayerInfo;

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
	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void handlerQuery(PermQueryPlayer event)
	{
		// actually handle stuff here.

		// if we have a permission area that contains this point:
		// if we can not perform this action in this area:
		// -> Deny the action

		// gets the result of the player in the area.

		if (event.getClass().getName().equals(PermQueryArea.class.getName()))
		{
			PermQueryArea eArea = (PermQueryArea) event;
			
			if (eArea.allOrNothing)
			{
				Zone zone = ZoneManager.getWhichZoneIn(eArea.doneTo, event.doer.worldObj);
				Result result = getResultFromZone(zone, event.permission, event.doer);
				event.setResult(result);
			}
			else
			{
				eArea.applicable = getApplicableZones(eArea.permission, eArea.doer, eArea.doneTo);
				if (eArea.applicable == null)
					eArea.setResult(Result.DENY);
				else if (eArea.applicable.isEmpty())
					eArea.setResult(Result.ALLOW);
				else
					eArea.setResult(Result.DEFAULT);
			}
		}
		else
		{
			Zone zone = ZoneManager.getWhichZoneIn(event.getDoerPoint(), event.doer.worldObj);
			Result result = getResultFromZone(zone, event.permission, event.doer);
			event.setResult(result);
		}
	}

	/**
	 * 
	 * @param zone Zone to check permissions in.
	 * @param perm The permission to check.
	 * @param player Player to check/
	 * @return the result for the perm.
	 */
	private Result getResultFromZone(Zone zone, PermissionChecker perm, EntityPlayer player)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		Result result = Result.DEFAULT;
		Zone tempZone = zone;
		while (result.equals(Result.DEFAULT))
		{
			String group = info.getGroupForZone(tempZone);
			result = tempZone.getPlayerOverride(player, perm);

			if (result.equals(Result.DEFAULT)) // or group blankets
				result = tempZone.getGroupOverride(group, perm);

			if (result.equals(Result.DEFAULT))
				if (tempZone == ZoneManager.GLOBAL)
					result = Permission.getPermissionDefault(perm.name);
				else
					tempZone = tempZone.getParent();
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
				if (zone.hasChildThatContains(doneTo))
					continue;
				else
					zones.add(zone);

		switch (zones.size())
			{
			// no children of the world? return the worldZone
				case 0:
					{
						Result result = getResultFromZone(worldZone, perm, player);
						if (result.equals(Result.ALLOW))
							return applicable;
						else
							return null;
					}
				// only 1 usable Zone? use it.
				case 1:
					{
						Result result = getResultFromZone(zones.get(0), perm, player);
						if (result.equals(Result.ALLOW))
							return applicable;
						else
							return null;
					}
				// else.. get the applicable states.
				default:
					{
						for (Zone zone : zones)
							if (getResultFromZone(zone, perm, player).equals(Result.ALLOW))
								applicable.add(doneTo.getIntersection(zone));
					}
			}

		return applicable;
	}

	public static boolean checkPermAllowed(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult().equals(Result.ALLOW);
	}

	public static Result checkPermResult(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult();
	}

	/**
	 * Parent permissions need not be registerred.
	 * @param permName Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 * @param allow True if the permission is allowed by default
	 */
	public static void registerPermission(String permName, boolean allow)
	{
		Permission perm = new Permission(permName, allow);
		Permission.addDefaultPermission(perm);
	}

	/**
	 * This does NOT automatically register parents.
	 * @param perm Permission to be added. Best in form "ModName.parent1.parent2.parentN.name"
	 */
	public static void registerPermission(Permission perm)
	{
		assert !perm.allowed.equals(Result.DEFAULT) : new IllegalArgumentException("You cannot register a permission with a default value of DEFAULT!");
		Permission.addDefaultPermission(perm);
	}
}
