package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.annotations.Expose;

/**
 * Point which stores dimension as well
 */
public class WorldPoint extends Point
{
	protected String dim;

    @Expose(serialize = false)
    protected World world;

    // ------------------------------------------------------------

    public WorldPoint(String dim2, int x, int y, int z)
    {
        super(x, y, z);
        dim = dim2;
    }

    public WorldPoint(String dimension, BlockPos location)
    {
        this(dimension, location.getX(), location.getY(), location.getZ());
    }

    public WorldPoint(World world, int x, int y, int z)
    {
        super(x, y, z);
        this.dim = world.dimension().location().toString();
        this.world = world;
    }

    public WorldPoint(World world, BlockPos location)
    {
        this(world, location.getX(), location.getY(), location.getZ());
    }

    public WorldPoint(Entity entity)
    {
        super(entity);
        this.dim = entity.level.dimension().location().toString();
        this.world = entity.level;
    }

    public WorldPoint(String dim, Vector3d vector)
    {
        super(vector);
        this.dim = dim;
    }

    public WorldPoint(WorldPoint other)
    {
        this(other.dim, other.x, other.y, other.z);
    }

    public WorldPoint(String dimension, Point point)
    {
        this(dimension, point.x, point.y, point.z);
    }

    public WorldPoint(WarpPoint other)
    {
        this(other.getDimension(), other.getBlockX(), other.getBlockY(), other.getBlockZ());
    }

    public WorldPoint(BlockEvent event)
    {
        this(event.getWorld(), event.getPos());
    }

    public WorldPoint(IWorld world2, BlockPos pos)
    {
        this (((ServerWorld) world2), pos);
    }

    public static WorldPoint create(CommandSource sender)
    {
        return new WorldPoint(sender.getLevel().dimension().location().toString(), sender.getPosition());
    }

    // ------------------------------------------------------------

    public String getDimension()
    {
        return dim;
    }

    public void setDimension(String dim)
    {
        this.dim = dim;
    }

    @Override
    public WorldPoint setX(int x)
    {
        return (WorldPoint) super.setX(x);
    }

    @Override
    public WorldPoint setY(int y)
    {
        return (WorldPoint) super.setY(y);
    }

    @Override
    public WorldPoint setZ(int z)
    {
        return (WorldPoint) super.setZ(z);
    }
    
    public World getWorld(){
        if (world != null && world.dimension().location().toString() != dim)
            return world;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        RegistryKey<World> registrykey = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim));
        world = server.getLevel(registrykey);
        if (world == null) {
            LoggingHandler.felog.debug("argument.dimension.invalid"+ dim);
            return null;
        } else {
           return world;
        }
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
        return getWorld().getBlockEntity(getBlockPos());
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
                        m.group(4),
                        (int) Double.parseDouble(m.group(1)),
                        (int) Double.parseDouble(m.group(2)),
                        (int) Double.parseDouble(m.group(3)));
            }
            catch (NumberFormatException e)
            {
                // do nothing 
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
        h = h * 31 + dim.hashCode();
        return h;
    }

}
