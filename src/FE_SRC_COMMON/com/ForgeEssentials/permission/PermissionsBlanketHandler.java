package com.ForgeEssentials.permission;

import java.util.ArrayList;

import net.minecraftforge.event.EventPriority;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PermQueryBlanketArea;
import com.ForgeEssentials.api.permissions.query.PermQueryBlanketSpot;
import com.ForgeEssentials.api.permissions.query.PermQueryBlanketZone;
import com.ForgeEssentials.api.permissions.query.PermSubscribe;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.WorldArea;

/**
 * This is the default catcher of all the ForgeEssentials Permission checks.
 * Mods can inherit from any of the ForgeEssentials Permissions and specify more
 * specific catchers to get first crack at handling them.
 * The handling performed here is limited to basic area permission checks, and
 * is not aware of anything else other mods add to the system.
 * @author AbrarSyed
 */
public final class PermissionsBlanketHandler
{
	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = { PermResult.UNKNOWN })
	public void handleQuery(PermQueryBlanketZone event)
	{
		PermResult result = getResultFromZone(event.toCheck, event.checker, event.checkForward);
		event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = { PermResult.UNKNOWN })
	public void handleQuery(PermQueryBlanketSpot event)
	{
		Zone zone = ZoneManager.getWhichZoneIn(event.spot);
		PermResult result = getResultFromZone(zone, event.checker, event.checkForward);
		event.setResult(result);
	}

	@PermSubscribe(priority = EventPriority.NORMAL, handleResult = { PermResult.UNKNOWN })
	public void handleQuery(PermQueryBlanketArea event)
	{
		if (event.allOrNothing)
		{
			Zone zone = ZoneManager.getWhichZoneIn(event.doneTo);
			PermResult result = getResultFromZone(zone, event.checker, event.checkForward);
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
	 * @param zone Zone to check permissions in.
	 * @param perm The permission to check.
	 * @param player Player to check/
	 * @return the result for the perm.
	 */
	private PermResult getResultFromZone(Zone zone, PermissionChecker checker, boolean checkForward)
	{
		PermResult result = PermResult.UNKNOWN;
		Zone tempZone = zone;
		while (result.equals(PermResult.UNKNOWN))
		{
			result = SqlHelper.getPermissionResult(PermissionsAPI.getDEFAULT().name, true, checker, tempZone.getZoneName(), checkForward);

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

	private ArrayList<AreaBase> getApplicableAreas(WorldArea doneTo, PermQueryBlanketArea event)
	{
		ArrayList<AreaBase> applicable = new ArrayList<AreaBase>();

		Zone worldZone = ZoneManager.getWorldZone(FunctionHelper.getDimension(doneTo.dim));
		ArrayList<Zone> zones = new ArrayList<Zone>();

		// add all children
		for (Zone zone : ZoneManager.getZoneList())
		{
			if (zone == null || zone.isGlobalZone() || zone.isWorldZone())
			{
				continue;
			}
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
						PermResult result = getResultFromZone(worldZone, event.checker, event.checkForward);
						if (result.equals(PermResult.ALLOW))
							return applicable;
						else
							return null;
					}
				// only 1 usable Zone? use it.
				case 1:
					{
						PermResult result = getResultFromZone(zones.get(0), event.checker, event.checkForward);
						if (result.equals(PermResult.ALLOW))
							return applicable;
						else
							return null;
					}
				// else.. get the applicable states.
				default:
					{
						for (Zone zone : zones)
						{
							if (getResultFromZone(zone, event.checker, event.checkForward).equals(PermResult.ALLOW))
							{
								applicable.add(doneTo.getIntersection(zone));
							}
						}
					}
			}

		return applicable;
	}
}
