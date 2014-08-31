package com.forgeessentials.api.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.Selection;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.FunctionHelper;
import net.minecraft.world.World;

@SaveableObject
public class Zone extends WorldArea implements Comparable<Object> {
    @SaveableField
    public int priority;    // lowest priority is 0

    @UniqueLoadingKey
    @SaveableField
    private String zoneID;    // unique string name

    @SaveableField
    public String parent;    // the unique name of the parent.

    public Zone(String name, Selection sel, Zone parent)
    {
        super(parent.dim, sel);
        zoneID = name;
        this.parent = parent.zoneID;
    }

    public Zone(String name, Selection sel, World world)
    {
        super(world, sel);
        zoneID = name;
        parent = FunctionHelper.getZoneWorldString(world);
    }

    /**
     * used to construct Global and World zones.
     *
     * @param name
     */
    public Zone(String name, int dimension)
    {
        super(dimension, new Point(0, 0, 0), new Point(0, 0, 0));
        zoneID = name;
        parent = APIRegistry.zones.getGLOBAL().zoneID;
    }

    /**
     * special one just for the SUPER and GLOBAL zones
     *
     * @param name
     */
    public Zone(String name)
    {
        super(0, new Point(0, 0, 0), new Point(0, 0, 0));
        zoneID = name;
        parent = null;
    }

    /**
     * used for reconstruct method only.
     *
     * @param sel
     * @param dim
     */
    private Zone(Selection sel, int dim)
    {
        super(dim, sel.getLowPoint(), sel.getHighPoint());
    }

    public boolean isParentOf(Zone zone)
    {
        if (parent == null)
        {
            return true;
        }
        else if (zone == null)
        {
            return false;
        }
        else if (zone.parent == null)
        {
            return false;
        }
        else if (zoneID.equals(zone.parent))
        {
            return true;
        }
        else if (zone.parent.equals(APIRegistry.zones.getGLOBAL().zoneID))
        {
            return false;
        }
        else
        {
            return isParentOf(APIRegistry.zones.getZone(zone.parent));
        }
    }

    /**
     * @return if this Permission is a child of the given Permission.
     */
    public boolean isChildOf(Zone zone)
    {
        if (zone.parent == null)
        {
            return false;
        }
        else if (zone.parent.equals(APIRegistry.zones.getGLOBAL().zoneID))
        {
            return dim == zone.dim;
        }
        else if (zone.zoneID.equals(parent))
        {
            return true;
        }
        else
        {
            return APIRegistry.zones.getZone(parent).isChildOf(zone);
        }
    }

    /**
     * @return The Unique ID of this Zone.
     */
    public String getZoneName()
    {
        return zoneID;
    }

    @Override
    public int compareTo(Object o)
    {
        if (!(o instanceof Zone))
        {
            return Integer.MIN_VALUE;
        }

        if (equals(o))
        {
            return 0;
        }

        Zone zone = (Zone) o;
        if (zone.isParentOf(this))
        {
            return 100;
        }
        else if (isParentOf(zone))
        {
            return -100;
        }
        else
        {
            return priority - zone.priority;
        }
    }

    public boolean isGlobalZone()
    {
        return parent == null;
    }

    public boolean isWorldZone()
    {
        if (parent == null)
        {
            return false;
        }
        return parent.equals(APIRegistry.zones.getGLOBAL().zoneID);
    }

    @Reconstructor
    private static Zone reconstruct(IReconstructData tag)
    {
        Point high = (Point) tag.getFieldValue("high");
        Point low = (Point) tag.getFieldValue("low");
        Selection sel = new Selection(high, low);
        int dim = (Integer) tag.getFieldValue("dim");

        Zone zone = new Zone(sel, dim);

        zone.zoneID = (String) tag.getFieldValue("zoneID");
        zone.parent = (String) tag.getFieldValue("parent");
        zone.priority = (Integer) tag.getFieldValue("priority");

        return zone;
    }

    @Override
    public String toString()
    {
        return zoneID + " " + super.toString();
    }
}
