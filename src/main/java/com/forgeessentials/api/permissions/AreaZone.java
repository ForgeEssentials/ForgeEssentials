package com.forgeessentials.api.permissions;

import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.AreaShape;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link AreaZone} covers just a specific area in one world. It has higher priority than all other {@link Zone} types. Area zones can overlap. Priority is then
 * decided by assigning highest priority to the innermost, smallest area.
 * 
 * @author Olee
 */
public class AreaZone extends Zone implements Comparable<AreaZone> {

    private WorldZone worldZone;

    private String name;

    private AreaBase area;

    private AreaShape shape = AreaShape.BOX;

    private int priority;

    private AreaZone(int id)
    {
        super(id);
    }

    public AreaZone(WorldZone worldZone, String name, AreaBase area, int id)
    {
        this(id);
        this.worldZone = worldZone;
        this.name = name;
        this.area = area;
        this.worldZone.addAreaZone(this);
    }

    public AreaZone(WorldZone worldZone, String name, AreaBase area)
    {
        this(worldZone, name, area, worldZone.getServerZone().nextZoneID());
    }

    @Override
    public boolean isInZone(WorldPoint point)
    {
        if (!worldZone.isInZone(point))
            return false;
        return shape.contains(area, point);
    }

    @Override
    public boolean isInZone(WorldArea otherArea)
    {
        if (!worldZone.isInZone(otherArea))
            return false;
        return shape.contains(area, otherArea);
    }

    @Override
    public boolean isPartOfZone(WorldArea otherArea)
    {
        if (!worldZone.isPartOfZone(otherArea))
            return false;
        return this.area.intersectsWith(otherArea);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return worldZone.getName() + "_" + name;
    }

    @Override
    public Zone getParent()
    {
        return worldZone;
    }

    @Override
    public ServerZone getServerZone()
    {
        return worldZone.getServerZone();
    }

    public String getShortName()
    {
        return name;
    }

    public WorldZone getWorldZone()
    {
        return worldZone;
    }

    public AreaBase getArea()
    {
        return area;
    }

    public void setArea(AreaBase area)
    {
        this.area = area;
        setDirty();
        getWorldZone().sortAreaZones();
    }

    public AreaShape getShape()
    {
        return shape;
    }

    public void setShape(AreaShape shape)
    {
        if (shape == null)
            this.shape = AreaShape.BOX;
        else
            this.shape = shape;
        setDirty();
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
        setDirty();
    }

    @Override
    public int compareTo(AreaZone otherArea)
    {
        int cmp = otherArea.priority - this.priority;
        if (cmp != 0)
            return cmp;

        Point areaSize = otherArea.getArea().getSize();
        Point thisSize = this.getArea().getSize();
        cmp = (thisSize.getX() * thisSize.getY()) - (areaSize.getX() * areaSize.getY());
        if (cmp != 0)
            return cmp;

        return cmp;
    }

}
