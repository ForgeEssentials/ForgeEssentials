package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Point {

    protected BlockPos blockPos;

	protected int x;

	protected int y;

	protected int z;

    // ------------------------------------------------------------

	public Point(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point (double x, double y, double z)
	{
		this.x = ((int) x);
		this.y = ((int) y);
		this.z = ((int) z);
	}

	public Point(Entity entity)
	{
		x = (int) Math.floor(entity.posX);
		y = (int) Math.floor(entity.posY);
		z = (int) Math.floor(entity.posZ);
	}

	public Point(Vec3 vector)
	{
		this((int) vector.xCoord, (int) vector.yCoord, (int) vector.zCoord);
	}

    public Point(Point other)
    {
        this(other.x, other.y, other.z);
    }

    // ------------------------------------------------------------

    public BlockPos getBlockPos()
    {
        if (blockPos == null)
            blockPos = new BlockPos(x, y, z);
        return blockPos;
    }
 
	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public Point setX(int x)
	{
		this.x = x;
        return this;
	}

	public Point setY(int y)
	{
		this.y = y;
        return this;
	}

	public Point setZ(int z)
	{
		this.z = z;
		return this;
	}

    // ------------------------------------------------------------

    /**
     * Returns the length of this vector
     */
    public double length()
    {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Returns the distance to another point
     */
    public double distance(Point v)
    {
        return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z));
    }

    public void add(Point v)
    {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void subtract(Point v)
    {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

	/**
	 * Checks if two points are on the same plane (have the same coordinate on at least one axis)
	 */
	public boolean alignsWith(Point point)
	{
		return x == point.x || y == point.y || z == point.z;
	}

	/**
	 * Checks if this point has greater or equal coordinates than another point on all axes
	 */
	public boolean isGreaterEqualThan(Point p)
	{
		return x >= p.x && y >= p.y && z >= p.z;
	}

    /**
     * Checks if this point has less or equal coordinates than another point on all axes
     */
	public boolean isLessEqualThan(Point p)
	{
		return x <= p.x && y <= p.y && z <= p.z;
	}

	public void validatePositiveY()
	{
		if (y < 0)
			y = 0;
	}

	public Vec3 toVec3()
	{
		return new Vec3(x, y, z);
	}

    // ------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    private static final Pattern pattern = Pattern.compile("\\s*\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\]\\s*");

    public static Point fromString(String value)
    {
        Matcher match = pattern.matcher(value);
        if (!match.matches())
            return null;
        return new Point(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)), Integer.parseInt(match.group(3)));
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Point)
        {
            Point p = (Point) object;
            return x == p.x && y == p.y && z == p.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1 + x;
        h = h * 31 + y;
        h = h * 31 + z;
        return h;
    }

}
