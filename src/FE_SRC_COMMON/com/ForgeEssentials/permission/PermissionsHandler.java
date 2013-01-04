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
	@PermSubscribe(priority = EventPriority.HIGHEST)
	public void doOpCheck(PermQueryPlayer event)
	{
		// TODO: opcheck.  AFTER MERGE
	}
	
	@PermSubscribe(priority = EventPriority.HIGH, handleResult = {PermResult.UNKNOWN})
	public void checkPlayerSupers(PermQueryPlayer event)
	{
		PermResult result = SqlHelper.getPermissionResult(event.doer.username, false, event.checker, ZoneManager.SUPER, event.checkForward);
		event.setResult(result);
	}
	
	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = {PermResult.UNKNOWN})
	public void handleQuery(PermQueryPlayer event)
	{
		
		// ensures its a permPlayerQuery before checking...
		if (event.getClass().getSimpleName().equals(PermQueryPlayer.class.getSimpleName()))
		{
			Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(event.doer), event.doer.worldObj);
			PermResult result = getResultFromZone(zone, event.checker, event.doer, event.checkForward);
			event.setResult(result);
		}
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = {PermResult.UNKNOWN})
	public void handleQuery(PermQueryPlayerZone event)
	{
		PermResult result = getResultFromZone(event.toCheck, event.checker, event.doer, event.checkForward);
		event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = {PermResult.UNKNOWN})
	public void handleQuery(PermQueryPlayerArea event)
	{
		if(FunctionHelper.isPlayerOp(event.doer.getCommandSenderName().toLowerCase()))
		{
			event.setResult(PermResult.ALLOW);
			return;
		}
		if (event.allOrNothing)
		{
			Zone zone = ZoneManager.getWhichZoneIn(event.doneTo, event.doer.worldObj);
			PermResult result = getResultFromZone(zone, event.checker, event.doer, event.checkForward);
			event.setResult(result);
		}
		else
		{
			event.applicable = getApplicableZones(event.checker, event.doer, event.doneTo, event.checkForward);
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
	private PermResult getResultFromZone(Zone zone, PermissionChecker perm, EntityPlayer player, boolean checkForward)
	{
		ArrayList<Group> groups;
		PermResult result = PermResult.UNKNOWN;
		Zone tempZone = zone;
		Group group;
		while (result.equals(PermResult.UNKNOWN))
		{
			// checks all parents as well.
			result = SqlHelper.getPermissionResult(player.username, false, perm, zone.getZoneID(), checkForward);

			if (result.equals(PermResult.UNKNOWN)) // check group permissions
			{
				groups = SqlHelper.getGroupsForPlayer(player.username, zone.getZoneID());
				for (int i = 0; result.equals(PermResult.UNKNOWN) && i < groups.size(); i++)
				{
					group = groups.get(i);
					result = SqlHelper.getPermissionResult(group.name, true, perm, zone.getZoneID(), checkForward);
				}
			}
			
			// still?? check defaults.
			if (result.equals(PermResult.UNKNOWN)) //&& !dOverride)
			{
				result = SqlHelper.getPermissionResult(PermissionsAPI.DEFAULT.name, true, perm, zone.getZoneID(), checkForward);
			}
			
			//	check parent.
			if (result.equals(PermResult.UNKNOWN))
				if (tempZone == ZoneManager.GLOBAL)
					result = PermResult.DENY;  // defaut deny...
				else
					tempZone = ZoneManager.getZone(tempZone.parent);  // get parent.
		}
		return result;
	}

	private ArrayList<AreaBase> getApplicableZones(PermissionChecker perm, EntityPlayer player, AreaBase doneTo, boolean checkForward)
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
						PermResult result = getResultFromZone(worldZone, perm, player, checkForward);
						if (result.equals(PermResult.ALLOW))
							return applicable;
						else
							return null;
					}
				// only 1 usable Zone? use it.
				case 1:
					{
						PermResult result = getResultFromZone(zones.get(0), perm, player, checkForward);
						if (result.equals(PermResult.ALLOW))
							return applicable;
						else
							return null;
					}
				// else.. get the applicable states.
				default:
					{
						for (Zone zone : zones)
							if (getResultFromZone(zone, perm, player, checkForward).equals(PermResult.ALLOW))
								applicable.add(doneTo.getIntersection(zone));
					}
			}

		return applicable;
	}
}
