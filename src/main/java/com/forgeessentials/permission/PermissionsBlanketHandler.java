package com.forgeessentials.permission;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.query.PermQuery;
import com.forgeessentials.api.permissions.query.PermQuery.PermResult;
import com.forgeessentials.api.permissions.query.PermQueryBlanketArea;
import com.forgeessentials.api.permissions.query.PermQueryBlanketSpot;
import com.forgeessentials.api.permissions.query.PermQueryBlanketZone;
import com.forgeessentials.util.AreaSelector.AreaBase;
import com.forgeessentials.util.AreaSelector.WorldArea;
import com.forgeessentials.util.FunctionHelper;

import java.util.ArrayList;

/**
 * This is the default catcher of all the ForgeEssentials Permission checks.
 * Mods can inherit from any of the ForgeEssentials Permissions and specify more
 * specific catchers to get first crack at handling them.
 * The handling performed here is limited to basic area permission checks, and
 * is not aware of anything else other mods add to the system.
 *
 * @author AbrarSyed
 */
public final class PermissionsBlanketHandler {
    public static void parseQuery(PermQuery query)
    {
        if (query instanceof PermQueryBlanketZone)
        {
            handleZone((PermQueryBlanketZone) query);
        }
        else if (query instanceof PermQueryBlanketArea)
        {
            handleArea((PermQueryBlanketArea) query);
        }
        else if (query instanceof PermQueryBlanketSpot)
        {
            handleSpot((PermQueryBlanketSpot) query);
        }
    }

    protected static void handleZone(PermQueryBlanketZone event)
    {
        PermResult result = getResultFromZone(event.toCheck, event.checker, event.checkForward);
        event.setResult(result);
    }

    protected static void handleSpot(PermQueryBlanketSpot event)
    {
        Zone zone = APIRegistry.zones.getWhichZoneIn(event.spot);
        PermResult result = getResultFromZone(zone, event.checker, event.checkForward);
        event.setResult(result);
    }

    protected static void handleArea(PermQueryBlanketArea event)
    {
        if (event.allOrNothing)
        {
            Zone zone = APIRegistry.zones.getWhichZoneIn(event.doneTo);
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
     * @param zone   Zone to check permissions in.
     * @param perm   The permission to check.
     * @param player Player to check/
     * @return the result for the perm.
     */
    private static PermResult getResultFromZone(Zone zone, PermissionChecker checker, boolean checkForward)
    {
        PermResult result = PermResult.UNKNOWN;
        Zone tempZone = zone;
        while (result.equals(PermResult.UNKNOWN))
        {
            result = SqlHelper.getPermissionResult(APIRegistry.perms.getDEFAULT().name, true, checker, tempZone.getZoneName(), checkForward);

            // still unknown? check parent zones.
            if (result.equals(PermResult.UNKNOWN))
            {
                if (tempZone == APIRegistry.zones.getGLOBAL())
                {
                    // default deny.
                    result = PermResult.DENY;
                }
                else
                {
                    // get the parent of the zone.
                    tempZone = APIRegistry.zones.getZone(tempZone.parent);
                }
            }
        }
        return result;
    }

    private static ArrayList<AreaBase> getApplicableAreas(WorldArea doneTo, PermQueryBlanketArea event)
    {
        ArrayList<AreaBase> applicable = new ArrayList<AreaBase>();

        Zone worldZone = APIRegistry.zones.getWorldZone(FunctionHelper.getDimension(doneTo.dim));
        ArrayList<Zone> zones = new ArrayList<Zone>();

        // add all children
        for (Zone zone : APIRegistry.zones.getZoneList())
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
            PermResult result = getResultFromZone(zones.get(0), event.checker, event.checkForward);
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
