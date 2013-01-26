package com.ForgeEssentials.permission;

import java.util.ArrayList;

import net.minecraftforge.event.EventPriority;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerArea;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayerZone;
import com.ForgeEssentials.api.permissions.query.PermSubscribe;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;

/**
 * 
 * This is the default catcher of all the ForgeEssentials Permission checks. Mods can inherit from any of the ForgeEssentials Permissions and specify more
 * specific catchers to get first crack at handling them.
 * 
 * The handling performed here is limited to basic area permission checks, and is not aware of anything else other mods add to the system.
 * 
 * @author AbrarSyed
 * 
 */
public final class PermissionsHandler
{
	@PermSubscribe(priority = EventPriority.HIGHEST)
	public void doOpCheck(PermQueryPlayer event)
	{
		boolean isOp = FunctionHelper.isPlayerOp(event.doer.username.toLowerCase());
		event.setResult(isOp ? PermResult.ALLOW : PermResult.UNKNOWN);
	}

	@PermSubscribe(priority = EventPriority.HIGH, handleResult = { PermResult.UNKNOWN })
	public void checkPlayerSupers(PermQueryPlayer event)
	{
		PermResult result = SqlHelper.getPermissionResult(event.doer.username, false, event.checker, ZoneManager.getSUPER().getZoneName(), event.checkForward);
		if (!result.equals(PermResult.UNKNOWN))
			event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = { PermResult.UNKNOWN })
	public void handleQuery(PermQueryPlayer event)
	{
		//OutputHandler.SOP("TEST!!!!");
		// ensures its a permPlayerQuery before checking...
		if (event.getClass().getSimpleName().equals(PermQueryPlayer.class.getSimpleName()))
		{
			Zone zone = ZoneManager.getWhichZoneIn(FunctionHelper.getEntityPoint(event.doer), event.doer.worldObj);
			PermResult result = getResultFromZone(zone, event);
			event.setResult(result);
		}
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = { PermResult.UNKNOWN })
	public void handleQuery(PermQueryPlayerZone event)
	{
		//OutputHandler.SOP("TEST!!!!");
		PermResult result = getResultFromZone(event.toCheck, event);
		event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = { PermResult.UNKNOWN })
	public void handleQuery(PermQueryPlayerArea event)
	{
		//OutputHandler.SOP("TEST!!!!");
		if (event.allOrNothing)
		{
			Zone zone = ZoneManager.getWhichZoneIn(event.doneTo, event.doer.worldObj);
			PermResult result = getResultFromZone(zone, event);
			event.setResult(result);
		}
		else
		{
			event.applicable = getApplicableAreas(event.doneTo, event);
			if (event.applicable == null)
			{
				event.setResult(PermResult.DENY);
			}
			else if (event.applicable.isEmpty())
			{
				event.setResult(PermResult.ALLOW);
			}
			else
			{
				event.setResult(PermResult.PARTIAL);
			}
		}
	}

	/**
	 * 
	 * @param zone Zone to check permissions in.
	 * @param perm The permission to check.
	 * @param player Player to check/
	 * @return the result for the perm.
	 */
	private PermResult getResultFromZone(Zone zone, PermQueryPlayer event)
	{
		ArrayList<Group> groups;
		PermResult result = PermResult.UNKNOWN;
		Zone tempZone = zone;
		Group group;
		while (result.equals(PermResult.UNKNOWN))
		{
			// get the permissions... Tis automatically checks permision parents...
			result = SqlHelper.getPermissionResult(event.doer.username, false, event.checker, zone.getZoneName(), event.checkForward);

			// if its unknown still
			if (result.equals(PermResult.UNKNOWN))
			{
				// get all the players groups here.
				groups = APIHelper.getApplicableGroups(event.doer, false);
				
				// iterates through the groups.
				for (int i = 0; result.equals(PermResult.UNKNOWN) && i < groups.size(); i++)
				{
					group = groups.get(i);
					while (group != null && result == PermResult.UNKNOWN)
					{
						// checks the permissions for the group.
						result = SqlHelper.getPermissionResult(group.name, true, event.checker, tempZone.getZoneName(), event.checkForward);
						
						// sets the group to its parent.
						group = SqlHelper.getGroupForName(group.parent);
					}
				}
			}

			// check defaults... unless it has the override..
			if (result.equals(PermResult.UNKNOWN) && !event.dOverride)
			{
				result = SqlHelper.getPermissionResult(APIHelper.DEFAULT.name, true, event.checker, zone.getZoneName(), event.checkForward);
			}

			// still unknown? check parent zones.
			if (result.equals(PermResult.UNKNOWN))
			{
				if (tempZone == ZoneManager.getGLOBAL())
				{
					// default deny.
					result = PermResult.DENY;
				}
				else
				{
					// get the parent of the zone.
					tempZone = ZoneManager.getZone(tempZone.parent);
				}
			}
		}
		return result;
	}

	private ArrayList<AreaBase> getApplicableAreas(AreaBase doneTo, PermQueryPlayer event)
	{
		ArrayList<AreaBase> applicable = new ArrayList<AreaBase>();

		Zone worldZone = ZoneManager.getWorldZone(event.doer.worldObj);
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all children
		Zone zone;
		for (String zID : ZoneManager.zoneSet())
		{
			zone = ZoneManager.getZone(zID);
			if (zone.intersectsWith(doneTo) && worldZone.isParentOf(zone))
			{
				zones.add(zone);
			}
		}

		switch (zones.size())
		{
		// no children of the world? return the worldZone
		case 0:
		{
			PermResult result = getResultFromZone(worldZone, event);
			if (result.equals(PermResult.ALLOW))
			{
				return applicable;
			}
			else
			{
				return null;
			}
		}
		// only 1 usable Zone? use it.
		case 1:
		{
			PermResult result = getResultFromZone(zones.get(0), event);
			if (result.equals(PermResult.ALLOW))
			{
				return applicable;
			}
			else
			{
				return null;
			}
		}
		// else.. get the applicable states.
		default:
		{
			for (Zone zone1 : zones)
			{
				if (getResultFromZone(zone1, event).equals(PermResult.ALLOW))
				{
					applicable.add(doneTo.getIntersection(zone1));
				}
			}
		}
		}

		return applicable;
	}
}
