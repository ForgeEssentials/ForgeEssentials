package com.forgeessentials.jscripting.fewrapper.fe;


import net.minecraft.entity.Entity;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntity;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorldServer;
import com.forgeessentials.teleport.CommandWarp.Warp;

public class JsWarpPoint extends JsWrapper<WarpPoint>
{
    public JsWarpPoint(WarpPoint that)
    {
        super(that);
    }

    public JsWorldPoint<WorldPoint> toWorldPoint()
    {
        return new JsWorldPoint<>(that.toWorldPoint());
    }

    private JsWorldServer getWorld()
    {
        return new JsWorldServer(that.getWorld());
    }

    public int getBlockX()
    {
        return that.getBlockX();
    }

    private int getBlockY()
    {
        return that.getBlockY();
    }

    private int getBlockZ()
    {
        return that.getBlockZ();
    }

    public double getX()
    {
        return that.getX();
    }

    public double getY()
    {
        return that.getY();
    }

    public  double getZ()
    {
        return that.getZ();
    }

    public int getDimension()
    {
        return that.getDimension();
    }

    public float getPitch()
    {
        return that.getPitch();
    }

    public float getYaw()
    {
        return that.getYaw();
    }

    public void set(int dim, double xd, double yd, double zd, float pitch, float yaw)
    {
        that.set(dim, xd, yd, zd, pitch, yaw);
    }

    public void setDimension(int dim)
    {
        that.setDimension(dim);
    }

    public void setX(double value)
    {
        that.setX(value);
    }

    public void setY(double value)
    {
        that.setY(value);
    }

    public void setZ(double value)
    {
        that.setZ(value);
    }

    public void setPitch(float value)
    {
        that.setPitch(value);
    }

    public void setYaw(float value)
    {
        that.setYaw(value);
    }

    public double length()
    {
        return that.length();
    }

    public double distance(JsWarpPoint v)
    {
        return that.distance(v.that);
    }

    public double distance(JsEntity<Entity> e)
    {
        return that.distance(e.getThat());
    }

    public String toString()
    {
        return that.toString();
    }

    public String toReadableString()
    {
        return that.toReadableString();
    }

    public static JsWarpPoint fromString(String value)
    {
        return new JsWarpPoint(WarpPoint.fromString(value));
    }
}
