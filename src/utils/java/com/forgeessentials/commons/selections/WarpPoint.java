package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.gson.annotations.Expose;

public class WarpPoint
{

    protected int dim;

    protected float pitch;

    protected float yaw;

    protected double xd;

    protected double yd;

    protected double zd;

    @Expose(serialize = false)
    protected WorldServer world;

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

    public WarpPoint(WorldServer world, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.world = world;
        this.dim = world.provider.getDimensionId();
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(int dimension, BlockPos location, float pitch, float yaw)
    {
        this(dimension, location.getX() + 0.5, location.getY(), location.getZ() + 0.5, pitch, yaw);
    }

    public WarpPoint(Point point, int dimension, float pitch, float yaw)
    {
        this(dimension, point.getX(), point.getY(), point.getZ(), pitch, yaw);
    }

    public WarpPoint(WorldPoint point, float pitch, float yaw)
    {
        this(point.getDimension(), point.getX() + 0.5, point.getY(), point.getZ() + 0.5, pitch, yaw);
    }

    public WarpPoint(WorldPoint point)
    {
        this(point, 0, 0);
    }

    public WarpPoint(Entity entity)
    {
        this(entity.worldObj instanceof WorldServer ? (WorldServer) entity.worldObj : null, entity.posX, entity.posY, entity.posZ, entity.rotationPitch,
                entity.rotationYaw);
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
    
    public BlockPos getBlockPos()
    {
        return new BlockPos(getBlockX(), getBlockY(), getBlockZ());
    }

    public int getBlockX()
    {
        return (int) Math.floor(xd);
    }

    public int getBlockY()
    {
        return (int) Math.floor(yd);
    }

    public int getBlockZ()
    {
        return (int) Math.floor(zd);
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void set(int dim, double xd, double yd, double zd, float pitch, float yaw)
    {
        this.dim = dim;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void setDimension(int dim)
    {
        this.dim = dim;
    }

    public WorldServer getWorld()
    {
        if (world == null || world.provider.getDimensionId() != dim)
            world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dim);
        return world;
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
        return new Vec3(xd, yd, zd);
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

    public String toReadableString()
    {
        return String.format("%.0f %.0f %.0f dim=%d", xd, yd, zd, dim);
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
            return (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
        }
        if (object instanceof WorldPoint)
        {
            WorldPoint p = (WorldPoint) object;
            return dim == p.getDimension() && (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int h = 1 + Double.valueOf(xd).hashCode();
        h = h * 31 + Double.valueOf(yd).hashCode();
        h = h * 31 + Double.valueOf(zd).hashCode();
        h = h * 31 + Double.valueOf(pitch).hashCode();
        h = h * 31 + Double.valueOf(yaw).hashCode();
        h = h * 31 + dim;
        return h;
    }

    private static final Pattern fromStringPattern = Pattern.compile(
            "\\[(-?[\\d.]+),(-?[\\d.]+),(-?[\\d.]+),dim=(-?\\d+),pitch=(-?[\\d.]+),yaw=(-?[\\d.]+)\\]");

    public static WarpPoint fromString(String value)
    {
        value = value.replaceAll("\\s ", "");
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WarpPoint(
                        Integer.parseInt(m.group(4)),
                        Double.parseDouble(m.group(1)),
                        Double.parseDouble(m.group(2)),
                        Double.parseDouble(m.group(3)),
                        Float.parseFloat(m.group(5)),
                        Float.parseFloat(m.group(6)));
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
        else
        {
            WorldPoint worldPoint = WorldPoint.fromString(value);
            if (worldPoint == null)
                return null;
            return new WarpPoint(worldPoint);
        }
    }

}