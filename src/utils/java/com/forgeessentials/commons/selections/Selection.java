package com.forgeessentials.commons.selections;

import net.minecraft.world.level.Level;

public class Selection extends WorldArea
{

    private Point start;

    private Point end;

    public Selection(String dim, Point start, Point end)
    {
        super(dim, start == null ? (end == null ? new Point(0, 0, 0) : end) : start,
                end == null ? (start == null ? new Point(0, 0, 0) : start) : end);
        this.start = start;
        this.end = end;
    }

    public Selection(Level world, AreaBase area)
    {
        this(world.dimension().location().toString(), area.getLowPoint(), area.getHighPoint());
    }

    public Point getStart()
    {
        return start;
    }

    public Point getEnd()
    {
        return end;
    }

    public void setStart(Point start)
    {
        this.start = start;
        start.validatePositiveY();
        redefine(this.start, end);
    }

    public void setEnd(Point end)
    {
        this.end = end;
        end.validatePositiveY();
        redefine(start, this.end);
    }

    public boolean isValid()
    {
        return start != null && end != null && dim != null;
    }

}
