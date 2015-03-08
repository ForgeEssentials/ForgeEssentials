package com.forgeessentials.client.core;

import java.util.HashMap;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Clone of the PlayerInfo for the client only.
 *
 * @author AbrarSyed
 */
@SideOnly(value = Side.CLIENT)
public class PlayerInfoClient
{
    public boolean playerLogger = false;

    private Selection selection;

    /*
     * Int => Type of change. (color in cui)
     * 0 = place
     * 1 = break
     * 2 = interact
     */
    public HashMap<Point, Integer> rbList = new HashMap<Point, Integer>();

    public Selection getSelection()
    {
        return selection;
    }
    
    public void setSelection(Selection selection)
    {
        this.selection = selection;
    }


}
