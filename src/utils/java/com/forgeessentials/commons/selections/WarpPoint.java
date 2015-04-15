package com.forgeessentials.commons.selections;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class WarpPoint {
    
    protected int dim;

    protected float pitch;

    protected float yaw;

    protected double xd;

    protected double yd;

    protected double zd;

    // ------------------------------------------------------------

    public WarpPoint(int dimension, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.dim = dimension;
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(Point point, int dimension, float playerPitch, float playerYaw)
    {
        this(dimension, point.getX(), point.getY(), point.getZ(), playerPitch, playerYaw);
    }

    public WarpPoint(WorldPoint point, float playerPitch, float playerYaw)
    {
        this(point.getDimension(), point.getX() + 0.5, point.getY(), point.getZ() + 0.5, playerPitch, playerYaw);
    }

    public WarpPoint(WorldPoint point)
    {
        this(point, 0, 0);
    }

    public WarpPoint(Entity sender)
    {
        dim = sender.dimension;
        xd = sender.posX;
        yd = sender.posY;
        zd = sender.posZ;
        pitch = sender.rotationPitch;
        yaw = sender.rotationYaw;
    }

    public WarpPoint(WarpPoint point)
    {
        this(point.dim, point.xd, point.yd, point.zd, point.pitch, point.yaw);
    }

    // ------------------------------------------------------------
    
    public int getDimension()
    {
        return dim;
    }

    public double getX()
    {
        return xd;
    }

    public double getY()
    {
        return yd;
    }

    public double getZ()
    {
        return zd;
    }

    public int getBlockX()
    {
        return (int) xd;
    }

    public int getBlockY()
    {
        return (int) yd;
    }

    public int getBlockZ()
    {
        return (int) zd;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }
    
    public void setDimension(int dim)
    {
        this.dim = dim;
    }

    public void setX(double value)
    {
        xd = value;
    }

    public void setY(double value)
    {
        yd = value;
    }

    public void setZ(double value)
    {
        zd = value;
    }

    public void setPitch(float value)
    {
        pitch = value;
    }

    public void setYaw(float value)
    {
        yaw = value;
    }

    // ------------------------------------------------------------

    /**
     * Returns the length of this vector
     */
    public double length()
    {
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    /**
     * Returns the distance to another point
     */
    public double distance(WarpPoint v)
    {
        return Math.sqrt((xd - v.xd) * (xd - v.xd) + (yd - v.yd) * (yd - v.yd) + (zd - v.zd) * (zd - v.zd));
    }

    /**
     * Returns the distance to another entity
     */
    public double distance(Entity e)
    {
        return Math.sqrt((xd - e.posX) * (xd - e.posX) + (yd - e.posY) * (yd - e.posY) + (zd - e.posZ) * (zd - e.posZ));
    }

    public void validatePositiveY()
    {
        if (yd < 0)
            yd = 0;
    }

    public Vec3 toVec3()
    {
        return Vec3.createVectorHelper(xd, yd, zd);
    }

    public WorldPoint toWorldPoint()
    {
        return new WorldPoint(this);
    }

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + xd + "," + yd + "," + zd + ",dim=" + dim + ",pitch=" + pitch + ",yaw=" + yaw + "]";
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof WarpPoint)
        {
            WarpPoint p = (WarpPoint) object;
            return xd == p.xd && yd == p.yd && zd == p.zd;
        }
        if (object instanceof Point)
        {
            Point p = (Point) object;
            return xd == p.getX() && yd == p.getY() && zd == p.getZ();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1 + Double.hashCode(xd);
        h = h * 31 + Double.hashCode(yd);
        h = h * 31 + Double.hashCode(zd);
        h = h * 31 + Double.hashCode(pitch);
        h = h * 31 + Double.hashCode(yaw);
        h = h * 31 + dim;
        return h;
    }


}
