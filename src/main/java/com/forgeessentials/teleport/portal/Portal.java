package com.forgeessentials.teleport.portal;

import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;

/**
 * 
 * 
 * @author Olee
 */
public class Portal {

    protected NamedWorldArea portalArea;
    
    protected NamedWorldPoint target;

    public Portal(NamedWorldArea portalArea, NamedWorldPoint target)
    {
        this.portalArea = portalArea;
        this.target = target;
    }

    public Portal(WorldArea portalArea, WorldPoint target)
    {
        this(new NamedWorldArea(portalArea), new NamedWorldPoint(target));
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

}
