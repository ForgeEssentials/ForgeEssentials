package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.google.gson.annotations.Expose;

public class WarpPoint
{

    protected String dim;

    protected float pitch;

    protected float yaw;

    protected double xd;

    protected double yd;

    protected double zd;

    @Expose(serialize = false)
    protected ServerLevel world;

    // ------------------------------------------------------------

    public WarpPoint(String dimension, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.dim = dimension;
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(ServerLevel world, double x, double y, double z, float playerPitch, float playerYaw)
    {
        this.world = world;
        this.dim = world.dimension().location().toString();
        this.xd = x;
        this.yd = y;
        this.zd = z;
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(ServerLevel world, BlockPos pos, float playerPitch, float playerYaw)
    {
        this.world = world;
        this.dim = world.dimension().location().toString();
        this.xd = pos.getX();
        this.yd = pos.getY();
        this.zd = pos.getZ();
        this.pitch = playerPitch;
        this.yaw = playerYaw;
    }

    public WarpPoint(String dimension, BlockPos location, float pitch, float yaw)
    {
        this(dimension, location.getX() + 0.5, location.getY(), location.getZ() + 0.5, pitch, yaw);
    }

    public WarpPoint(ResourceKey<Level> dimension, BlockPos location, float pitch, float yaw)
    {
        this(dimension.location().toString(), location.getX() + 0.5, location.getY(), location.getZ() + 0.5, pitch,
                yaw);
    }

    public WarpPoint(Point point, String dimension, float pitch, float yaw)
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
        this(entity.level instanceof ServerLevel ? (ServerLevel) entity.level : null, entity.position().x,
                entity.position().y, entity.position().z, entity.xRotO, entity.yRotO);
    }

    public WarpPoint(WarpPoint point)
    {
        this(point.dim, point.xd, point.yd, point.zd, point.pitch, point.yaw);
    }

    // ------------------------------------------------------------

    public String getDimension()
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

    public void set(String dim, double xd, double yd, double zd, float pitch, float yaw)
    {
        this.dim = dim;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void setDimension(String dim)
    {
        this.dim = dim;
    }

    public ServerLevel getWorld()
    {
        if (world != null && world.dimension().location().toString().equals(dim))
            return world.getLevel();
        world = ServerLifecycleHooks.getCurrentServer()
                .getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim)));
        if (world == null)
        {
            System.out.println("argument.dimension.invalid" + dim);
            return null;
        }
        else
        {
            return world.getLevel();
        }

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
        return Math.sqrt((xd - e.position().x) * (xd - e.position().x) + (yd - e.position().y) * (yd - e.position().y)
                + (zd - e.position().z) * (zd - e.position().z));
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
        return String.format("%.0f %.0f %.0f dim=%s", xd, yd, zd, dim);
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
            return dim.equals(p.getDimension()) && (int) xd == p.getX() && (int) yd == p.getY() && (int) zd == p.getZ();
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
        h = h * 31 + dim.hashCode();
        return h;
    }

    private static final Pattern fromStringPattern = Pattern
            .compile("\\[(-?[\\d.]+),(-?[\\d.]+),(-?[\\d.]+),dim=([A-Za-z0-9:]+),pitch=(-?[\\d.]+),yaw=(-?[\\d.]+)\\]");

    public static WarpPoint fromString(String value)
    {
        value = value.replaceAll("\\s ", "");
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WarpPoint(m.group(4), Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)),
                        Double.parseDouble(m.group(3)), Float.parseFloat(m.group(5)), Float.parseFloat(m.group(6)));
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
