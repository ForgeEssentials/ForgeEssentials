package com.forgeessentials.commons.selections;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

@SaveableObject(SaveInline = true)
public class WarpPoint {
    
    @SaveableField
    protected int dim;

    @SaveableField
    protected float pitch;

    @SaveableField
    protected float yaw;

    @SaveableField
    protected double xd;

    @SaveableField
    protected double yd;

    @SaveableField
    protected double zd;

    public WarpPoint(int dimension, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.dim = dimension;
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(Point p, int dimension, float playerPitch, float playerYaw)
    {
        this(dimension, p.getX(), p.getY(), p.getZ(), playerPitch, playerYaw);
    }

    public WarpPoint(WorldPoint p, float playerPitch, float playerYaw)
    {
        this(p.dim, p.getX(), p.getY(), p.getZ(), playerPitch, playerYaw);
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

    /**
     * This is calculated by the whichever has higher coords.
     *
     * @return Posotive number if this Point is larger. 0 if they are equal.
     * Negative if the provided point is larger.
     */
    public int compareTo(WarpPoint point)
    {
        if (equals(point))
        {
            return 0;
        }

        int positives = 0;
        int negatives = 0;

        if (xd > point.xd)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (yd > point.yd)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (zd > point.zd)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (positives > negatives)
        {
            return +1;
        }
        else if (negatives > positives)
        {
            return -1;
        }
        else
        {
            return (int) (xd - point.xd + (yd - point.yd) + (zd - point.zd));
        }
    }

    /**
     * gets a new Point with the same data as the provided one.
     *
     * @param point
     * @return
     */
    public static WarpPoint copy(WarpPoint point)
    {
        return new WarpPoint(point.dim, point.xd, point.yd, point.zd, point.pitch, point.yaw);
    }

    /**
     * ensures the Point is valid. Just floors the Y axis to 0. Y can't be
     * negative.
     */
    public void validate()
    {
        if (yd < 0)
        {
            yd = 0;
        }
    }

    /**
     * @param point
     * @return The distance to a given Block.
     */
    public double getDistanceTo(WarpPoint point)
    {
        return Math.sqrt((xd - point.xd) * (xd - point.xd) + (yd - point.yd) * (yd - point.yd) + (zd - point.zd) * (zd - point.zd));
    }

    /**
     * @param point
     * @return The distance to a given Block.
     */
    public double getDistanceTo(Entity e)
    {
        return Math.sqrt((xd - e.posX) * (xd - e.posX) + (yd - e.posY) * (yd - e.posY) + (zd - e.posZ) * (zd - e.posZ));
    }

    @Reconstructor()
    public static WarpPoint reconstruct(IReconstructData tag)
    {
        double x = (Double) tag.getFieldValue("xd");
        double y = (Double) tag.getFieldValue("yd");
        double z = (Double) tag.getFieldValue("zd");
        int dim = (Integer) tag.getFieldValue("dim");
        float pitch = (Float) tag.getFieldValue("pitch");
        float yaw = (Float) tag.getFieldValue("yaw");
        return new WarpPoint(dim, x, y, z, pitch, yaw);
    }

    @UniqueLoadingKey()
    private String getLoadingField()
    {
        return "WarpPoint" + this;
    }

    @Override
    public String toString()
    {
        return "WarpPoint[" + dim + "," + xd + "," + yd + "," + zd + "," + pitch + "," + yaw + "]";
    }

    public Vec3 toVec3()
    {
        return Vec3.createVectorHelper(xd, yd, zd);
    }

    
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

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void setPitch(float value)
    {
        pitch = value;
    }

    public void setYaw(float value)
    {
        yaw = value;
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

    public WorldPoint toWorldPoint()
    {
        return new WorldPoint(dim, (int) xd, (int) yd, (int) zd);
    }

}
