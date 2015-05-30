package com.forgeessentials.teleport.portal;

import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;

/**
 * 
 */
public class Portal
{

    protected NamedWorldArea portalArea;

    protected NamedWorldPoint target;

    protected boolean frame = true;

    public Portal(NamedWorldArea portalArea, NamedWorldPoint target, boolean frame)
    {
        this.portalArea = portalArea;
        this.target = target;
        this.frame = frame;
    }

    public Portal(WorldArea portalArea, WorldPoint target, boolean frame)
    {
        this(new NamedWorldArea(portalArea), new NamedWorldPoint(target), frame);
    }

    public NamedWorldArea getPortalArea()
    {
        return portalArea;
    }

    public void setPortalArea(NamedWorldArea portalArea)
    {
        this.portalArea = portalArea;
    }

    public NamedWorldPoint getTarget()
    {
        return target;
    }

    public void setTarget(NamedWorldPoint target)
    {
        this.target = target;
    }

    public boolean hasFrame()
    {
        return frame;
    }

    public void setFrame(boolean frame)
    {
        this.frame = frame;
    }

}
