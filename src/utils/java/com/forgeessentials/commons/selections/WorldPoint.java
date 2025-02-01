package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.BlockEvent;

import com.google.gson.annotations.Expose;

/**
 * Point which stores dimension as well
 */
public class WorldPoint extends Point
{

    protected int dim;

    @Expose(serialize = false)
    protected World world;

    // ------------------------------------------------------------

    public WorldPoint(int dimension, int x, int y, int z)
    {
        super(x, y, z);
        dim = dimension;
    }

    public WorldPoint(int dimension, BlockPos location)
    {
        this(dimension, location.getX(), location.getY(), location.getZ());
    }

    public WorldPoint(World world, int x, int y, int z)
    {
        super(x, y, z);
        this.dim = world.provider.getDimensionId();
        this.world = world;
    }

    public WorldPoint(World world, BlockPos location)
    {
        this(world, location.getX(), location.getY(), location.getZ());
    }

    public WorldPoint(Entity entity)
    {
        super(entity);
        this.dim = entity.dimension;
        this.world = entity.worldObj;
    }

    public WorldPoint(int dim, Vec3 vector)
    {
        super(vector);
        this.dim = dim;
    }

    public WorldPoint(WorldPoint other)
    {
        this(other.dim, other.x, other.y, other.z);
    }

    public WorldPoint(int dimension, Point point)
    {
        this(dimension, point.x, point.y, point.z);
    }

    public WorldPoint(WarpPoint other)
    {
        this(other.getDimension(), other.getBlockX(), other.getBlockY(), other.getBlockZ());
    }

    public WorldPoint(BlockEvent event)
    {
        this(event.world, event.pos.getX(), event.pos.getY(), event.pos.getZ());
    }

    public static WorldPoint create(ICommandSender sender)
    {
        return new WorldPoint(sender.getEntityWorld(), sender.getPosition());
    }

    // ------------------------------------------------------------

    public int getDimension()
    {
        return dim;
    }

    public void setDimension(int dim)
    {
        this.dim = dim;
    }

    @Override
    public WorldPoint setX(int x)
    {
        this.x = x;
        return this;
    }

    @Override
    public WorldPoint setY(int y)
    {
        this.y = y;
        return this;
    }

    @Override
    public WorldPoint setZ(int z)
    {
        this.z = z;
        return this;
    }

    public World getWorld()
    {
        if (world != null && world.provider.getDimensionId() != dim)
            return world;
        world = DimensionManager.getWorld(dim);
        return world;
    }

    public WarpPoint toWarpPoint(float rotationPitch, float rotationYaw)
    {
        return new WarpPoint(this, rotationPitch, rotationYaw);
    }

    public Block getBlock()
    {
        return getWorld().getBlockState(getBlockPos()).getBlock();
    }

    public TileEntity getTileEntity()
    {
        return getWorld().getTileEntity(getBlockPos());
    }

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + x + "," + y + "," + z + ",dim=" + dim + "]";
    }

    private static final Pattern fromStringPattern = Pattern.compile("\\[(-?[\\d.]+),(-?[\\d.]+),(-?[\\d.]+),dim=(-?\\d+)\\]");

    public static WorldPoint fromString(String value)
    {
        value = value.replaceAll("\\s ", "");
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WorldPoint(
                        Integer.parseInt(m.group(4)),
                        (int) Double.parseDouble(m.group(1)),
                        (int) Double.parseDouble(m.group(2)),
                        (int) Double.parseDouble(m.group(3)));
            }
            catch (NumberFormatException e)
            {
                /* do nothing */
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof WorldPoint)
        {
            WorldPoint p = (WorldPoint) object;
            return dim == p.dim && x == p.x && y == p.y && z == p.z;
        }
        if (object instanceof WarpPoint)
        {
            WarpPoint p = (WarpPoint) object;
            return dim == p.dim && x == p.getBlockX() && y == p.getBlockY() && z == p.getBlockZ();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int h = 1 + x;
        h = h * 31 + y;
        h = h * 31 + z;
        h = h * 31 + dim;
        return h;
    }

}
