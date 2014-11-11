package com.forgeessentials.client.core;

import com.forgeessentials.commons.Point;
import com.forgeessentials.commons.Selection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashMap;

/**
 * Clone of the PlayerInfo for the client only.
 *
 * @author AbrarSyed
 */
@SideOnly(value = Side.CLIENT)
public class PlayerInfoClient
{
    public boolean playerLogger = false;

    // selection stuff
    private Point sel1;
    private Point sel2;
    private Selection selection;

    /*
     * Int => Type of change. (color in cui)
     * 0 = place
     * 1 = break
     * 2 = interact
     */
    public HashMap<Point, Integer> rbList = new HashMap<Point, Integer>();

    public PlayerInfoClient()
    {
        sel1 = null;
        sel2 = null;
        selection = null;
    }

    public Point getPoint1()
    {
        return sel1;
    }

    public void setPoint1(Point sel1)
    {
        this.sel1 = sel1;
        if (sel1 != null && sel2 != null)
        {
            selection = new Selection(sel1, sel2);
        }
    }

    public Point getPoint2()
    {
        return sel2;
    }

    public void setPoint2(Point sel2)
    {
        this.sel2 = sel2;
        if (sel1 != null && sel2 != null)
        {
            selection = new Selection(sel1, sel2);
        }
    }

    public Selection getSelection()
    {
        return selection;
    }

    public void clearSelection()
    {
        selection = null;
        sel1 = null;
        sel2 = null;
    }
}
