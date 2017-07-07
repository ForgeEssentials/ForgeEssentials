package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;

public class JsPlayerInfo extends JsWrapper<PlayerInfo>
{
    public JsPlayerInfo(PlayerInfo that)
    {
        super(that);
    }

    public JsPlayerInfo(UUID that)
    {
        super(PlayerInfo.get(that));
    }

    public JsUserIdent getUserIdent()
    {
        return new JsUserIdent(that.ident);
    }

    public Date getFirstLogin()
    {
        return that.getFirstLogin();
    }

    public Date getLastLogin()
    {
        return that.getLastLogin();
    }

    public Date getLastLogout()
    {
        return that.getLastLogout();
    }

    public long getTimePlayed()
    {
        return that.getTimePlayed();
    }

    public void setActive()
    {
        that.setActive();
    }

    public void setActive(long delta)
    {
        that.setActive(delta);
    }

    public long getInactiveTime()
    {
        return that.getInactiveTime();
    }

    public void removeTimeout(String name)
    {
        that.removeTimeout(name);
    }

    public boolean checkTimeout(String name)
    {
        return that.checkTimeout(name);
    }

    public long getRemainingTimeout(String name)
    {
        return that.getRemainingTimeout(name);
    }

    public void startTimeout(String name, long milliseconds)
    {
        that.startTimeout(name, milliseconds);
    }

    public boolean isWandEnabled()
    {
        return that.isWandEnabled();
    }

    public void setWandEnabled(boolean wandEnabled)
    {
        that.setWandEnabled(wandEnabled);
    }

    public String getWandID()
    {
        return that.getWandID();
    }

    public void setWandID(String wandID)
    {
        that.setWandID(wandID);
    }

    public int getWandDmg()
    {
        return that.getWandDmg();
    }

    public void setWandDmg(int wandDmg)
    {
        that.setWandDmg(wandDmg);
    }

    public JsPoint getSel1()
    {
        return new JsPoint(that.getSel1());
    }

    public JsPoint getSel2()
    {
        return new JsPoint(that.getSel2());
    }

    public int getSelDim()
    {
        return that.getSelDim();
    }

    public void setSel1(JsPoint<Point> point)
    {
        that.setSel1(point.getThat());
    }

    public void setSel2(JsPoint<Point> point)
    {
        that.setSel2(point.getThat());
    }

    public void setSelDim(int dimension)
    {
        that.setSelDim(dimension);
    }

    //TODO: Expose InventoryGroups to Js
    /*public Map<String, List<JsItemStack>> getModInventoryGroups()
    {

    }

    public List<JsItemStack> getInventoryGroupItems(String name)
    {

    }

    public String getInventoryGroup()
    {
        return that.getInventoryGroup();
    }

    public void setInventoryGroup(String name)
    {
        that.setInventoryGroup(name);
    }*/

    public JsWarpPoint getLastTeleportOrigin()
    {
        return new JsWarpPoint(that.getLastTeleportOrigin());
    }

    public void setLastTeleportOrigin(JsWarpPoint lastTeleportStart)
    {
        that.setLastTeleportOrigin(lastTeleportStart.getThat());
    }

    public JsWarpPoint getLastDeathLocation()
    {
        return new JsWarpPoint(that.getLastDeathLocation());
    }

    public void setLastDeathLocation(JsWarpPoint lastDeathLocation)
    {
        that.setLastDeathLocation(lastDeathLocation.getThat());
    }

    public long getLastTeleportTime()
    {
        return that.getLastTeleportTime();
    }

    public void setLastTeleportTime(long currentTimeMillis)
    {
        that.setLastTeleportTime(currentTimeMillis);
    }

    public JsWarpPoint getHome()
    {
        return new JsWarpPoint(that.getHome());
    }

    public void setHome(JsWarpPoint home)
    {
        that.setHome(home.getThat());
    }
}
