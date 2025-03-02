package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeLevel;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.google.gson.annotations.Expose;

/**
 * Point which stores dimension as well
 */
public class WorldPoint extends Point
{
    protected String dim;

    @Expose(serialize = false)
    protected Level world;

    public static WorldPoint NULL = new WorldPoint("overworld", 0, 0, 0);
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

    public WorldPoint(Level world, int x, int y, int z)
    {
        super(x, y, z);
        this.dim = world.dimension().location().toString();
        this.world = world;
    }

    public WorldPoint(Level world, BlockPos location)
    {
        this(world, location.getX(), location.getY(), location.getZ());
    }

    public WorldPoint(Entity entity)
    {
        super(entity);
        this.dim = entity.level.dimension().location().toString();
        this.world = entity.level;
    }

    public WorldPoint(ResourceKey<Level> dim, Vec3 vector)
    {
        super(vector);
        this.dim = dim.location().toString();
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

    public WorldPoint(IForgeLevel world2, BlockPos pos)
    {
        this(((ServerLevel) world2), pos);
    }

    public static WorldPoint create(CommandSource sender)
    {
        //No longer able to get position directly from command source
        if (sender instanceof Entity) {
            return new WorldPoint(((Entity) sender).level.dimension(), ((Entity) sender).getPosition(0));
        } else if (sender instanceof BaseCommandBlock) {
            return new WorldPoint(((BaseCommandBlock) sender).getLevel().dimension(),((BaseCommandBlock) sender).getPosition());
        }
        return WorldPoint.NULL;
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

    public void setDimension(ServerLevel dim)
    {
        this.dim = dim.dimension().location().toString();
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

    public Level getWorld()
    {
        if (world != null && world.dimension().location().toString().equals(dim))
            return world;
        world = ServerLifecycleHooks.getCurrentServer()
                .getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim)));
        if (world == null)
        {
            System.out.println("argument.dimension.invalid" + dim);
            return null;
        }
        else
        {
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

    public BlockEntity getTileEntity()
    {
        return getWorld().getBlockEntity(getBlockPos());
    }

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + x + "," + y + "," + z + ",dim=" + dim + "]";
    }

    private static final Pattern fromStringPattern = Pattern
            .compile("\\[(-?[\\d.]+),(-?[\\d.]+),(-?[\\d.]+),dim=([A-Za-z0-9:]+)\\]");

    public static WorldPoint fromString(String value)
    {
        value = value.replaceAll("\\s ", "");
        Matcher m = fromStringPattern.matcher(value);
        if (m.matches())
        {
            try
            {
                return new WorldPoint(m.group(4), (int) Double.parseDouble(m.group(1)),
                        (int) Double.parseDouble(m.group(2)), (int) Double.parseDouble(m.group(3)));
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
            return dim.equals(p.dim) && x == p.x && y == p.y && z == p.z;
        }
        if (object instanceof WarpPoint)
        {
            WarpPoint p = (WarpPoint) object;
            return dim.equals(p.dim) && x == p.getBlockX() && y == p.getBlockY() && z == p.getBlockZ();
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
