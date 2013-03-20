package com.ForgeEssentials.permission;

import java.util.ArrayList;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.api.permissions.query.PermQuery.PermResult;
import com.ForgeEssentials.api.permissions.query.PropQuery;
import com.ForgeEssentials.api.permissions.query.PropQueryBlanketSpot;
import com.ForgeEssentials.api.permissions.query.PropQueryBlanketZone;
import com.ForgeEssentials.api.permissions.query.PropQueryPlayer;
import com.ForgeEssentials.api.permissions.query.PropQueryPlayerSpot;
import com.ForgeEssentials.api.permissions.query.PropQueryPlayerZone;

public final class PermissionsPropHandler
{
	private PermissionsPropHandler()
	{
	}

	public static void handleQuery(PropQuery query)
	{
		Zone applied = null;
		String result = null;

		if (query instanceof PropQueryBlanketZone)
			applied = ((PropQueryBlanketZone) query).zone;
		else if (query instanceof PropQueryPlayerZone)
			applied = ((PropQueryPlayerZone) query).zone;
		else if (query instanceof PropQueryBlanketSpot)
			applied = ZoneManager.getWhichZoneIn(((PropQueryBlanketSpot) query).spot);
		else if (query instanceof PropQueryPlayerSpot)
			applied = ZoneManager.getWhichZoneIn(((PropQueryPlayerSpot) query).spot);

		if (query instanceof PropQueryPlayer)
		{
			result = getResultFromZone(applied, (PropQueryPlayer) query);
		}
		else if (query instanceof PropQueryBlanketZone)
		{
			result = getResultFromZone(applied, query.perm, ((PropQueryBlanketZone) query).checkParents);
		}
		else
		{
			result = getResultFromZone(applied, query.perm, true);
		}

		query.setValue(result);
	}

	public static Zone getZone(PropQuery query)
	{
		// this should never happen;
		return null;
	}

	private static Zone getZone(PropQueryPlayerZone query)
	{
		return query.zone;
	}

	private static Zone getZone(PropQueryPlayerSpot query)
	{
		return ZoneManager.getWhichZoneIn(query.spot);
	}

	private static Zone getZone(PropQueryBlanketSpot query)
	{
		return ZoneManager.getWhichZoneIn(query.spot);
	}

	private static Zone getZone(PropQueryBlanketZone query)
	{
		return query.zone;
	}

	/**
	 * @param zone Zone to check permProps in.
	 * @param perm The permission to check.
	 * @param player Player to check/
	 * @return the resulting permProp
	 */
	private static String getResultFromZone(Zone zone, PropQueryPlayer event)
	{
		ArrayList<Group> groups;
		String result = null;
		Zone tempZone = zone;
		Group group;
		while (result == null)
		{
			// get the permissions... Tis automatically checks permision
			// parents...
			result = SqlHelper.getPermissionProp(event.player.username, false, event.perm, tempZone.getZoneName());

			// if its unknown still
			if (result == null)
			{
				// get all the players groups here.
				groups = PermissionsAPI.getApplicableGroups(event.player, false);

				// iterates through the groups.
				for (int i = 0; result == null && i < groups.size(); i++)
				{
					group = groups.get(i);
					while (group != null && result == null)
					{
						// checks the permissions for the group.
						result = SqlHelper.getPermissionProp(group.name, true, event.perm, tempZone.getZoneName());

						// sets the group to its parent.
						group = SqlHelper.getGroupForName(group.parent);
					}
				}
			}

			// check defaults... unless it has the override..
			if (result == null)
			{
				result = SqlHelper.getPermissionProp(PermissionsAPI.getDEFAULT().name, true, event.perm, zone.getZoneName());
			}

			// still unknown? check parent zones.
			if (result == null)
			{
				if (tempZone == ZoneManager.getGLOBAL())
				{
					// default deny.
					result = "";
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

	/**
	 * @param zone Zone to check permissions in.
	 * @param perm The permission to check.
	 * @param player Player to check/
	 * @return the result for the perm.
	 */
	private static String getResultFromZone(Zone zone, String perm, boolean checkParents)
	{
		String result = null;
		Zone tempZone = zone;
		while (result == null)
		{
			result = SqlHelper.getPermissionProp(PermissionsAPI.getDEFAULT().name, true, perm, tempZone.getZoneName());

			// still unknown? check parent zones.
			if (result == null)
			{
				if (checkParents == false)
					return result;
				else if (tempZone == ZoneManager.getGLOBAL())
				{
					// default deny.
					result = null;
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
}
