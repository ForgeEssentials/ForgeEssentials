package com.forgeessentials.util.selections;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;

@SaveableObject(SaveInline = true)
public class WarpPoint {
    
    @SaveableField
    protected int dim;

    @SaveableField
    protected float pitch;

    @SaveableField
    protected float yaw;

    @SaveableField
    protected double x;

    @SaveableField
    protected double y;

    @SaveableField
    protected double z;

    public WarpPoint(int dimension, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.dim = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
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
        x = sender.posX;
        y = sender.posY;
        z = sender.posZ;
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

        if (x > point.x)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (y > point.y)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (z > point.z)
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
            return (int) (x - point.x + (y - point.y) + (z - point.z));
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
        return new WarpPoint(point.dim, point.x, point.y, point.z, point.pitch, point.yaw);
    }

    /**
     * ensures the Point is valid. Just floors the Y axis to 0. Y can't be
     * negative.
     */
    public void validate()
    {
        if (y < 0)
        {
            y = 0;
        }
    }

    /**
     * @param point
     * @return The distance to a given Block.
     */
    public double getDistanceTo(WarpPoint point)
    {
        return Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y) + (z - point.z) * (z - point.z));
    }

    /**
     * @param point
     * @return The distance to a given Block.
     */
    public double getDistanceTo(Entity e)
    {
        return Math.sqrt((x - e.posX) * (x - e.posX) + (y - e.posY) * (y - e.posY) + (z - e.posZ) * (z - e.posZ));
    }

    @Reconstructor()
    public static WarpPoint reconstruct(IReconstructData tag)
    {
        if (tag.getFieldValue("xd") != null)
        {
            // Temporary old data-loading
            double x = (Double) tag.getFieldValue("xd");
            double y = (Double) tag.getFieldValue("yd");
            double z = (Double) tag.getFieldValue("zd");
            int dim = (Integer) tag.getFieldValue("dim");
            float pitch = (Float) tag.getFieldValue("pitch");
            float yaw = (Float) tag.getFieldValue("yaw");
            return new WarpPoint(dim, x, y, z, pitch, yaw);
        }
        double x = (Double) tag.getFieldValue("x");
        double y = (Double) tag.getFieldValue("y");
        double z = (Double) tag.getFieldValue("z");
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
        return "WarpPoint[" + dim + "," + x + "," + y + "," + z + "," + pitch + "," + yaw + "]";
    }

    public Vec3 toVec3()
    {
        return Vec3.createVectorHelper(x, y, z);
    }

    
    public int getDimension()
    {
        return dim;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public void setX(double value)
    {
        x = value;
    }

    public void setY(double value)
    {
        y = value;
    }

    public void setZ(double value)
    {
        z = value;
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
        return (int) x;
    }

    public int getBlockY()
    {
        return (int) y;
    }

    public int getBlockZ()
    {
        return (int) z;
    }

    public WorldPoint toWorldPoint()
    {
        return new WorldPoint(dim, (int) x, (int) y, (int) z);
    }

}
