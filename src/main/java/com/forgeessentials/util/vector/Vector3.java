package com.forgeessentials.util.vector;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Vector3 extends Vector2 implements Cloneable {

	protected double z;

	public Vector3()
	{
		this(0, 0, 0);
	}

	public Vector3(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Entity par1)
	{
		x = par1.posX;
		y = par1.posY;
		z = par1.posZ;
	}

	public Vector3(TileEntity par1)
	{
		x = par1.xCoord;
		y = par1.yCoord;
		z = par1.zCoord;
	}

	public Vector3(Vec3 par1)
	{
		x = par1.xCoord;
		y = par1.yCoord;
		z = par1.zCoord;
	}

	public Vector3(MovingObjectPosition par1)
	{
		x = par1.blockX;
		y = par1.blockY;
		z = par1.blockZ;
	}

	public Vector3(ChunkCoordinates par1)
	{
		x = par1.posX;
		y = par1.posY;
		z = par1.posZ;
	}

	public double getZ()
	{
		return z;
	}

	@Deprecated
	public static Vector3 get(Entity par1)
	{
		return new Vector3(par1);
	}

	@Deprecated
	public static Vector3 get(TileEntity par1)
	{
		return new Vector3(par1);
	}

	@Deprecated
	public static Vector3 get(Vec3 par1)
	{
		return new Vector3(par1);
	}

	@Deprecated
	public static Vector3 get(MovingObjectPosition par1)
	{
		return new Vector3(par1);
	}

	@Deprecated
	public static Vector3 get(ChunkCoordinates par1)
	{
		return new Vector3(par1);
	}

	/**
	 * Gets the distance between two vectors
	 *
	 * @return The distance
	 */
	public static double distance(Vector3 par1, Vector3 par2)
	{
		double var2 = par1.getX() - par2.getX();
		double var4 = par1.getY() - par2.getY();
		double var6 = par1.getZ() - par2.getZ();
		return MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
	}

	public static Vector3 subtract(Vector3 par1, Vector3 par2)
	{
		return new Vector3(par1.getX() - par2.getX(), par1.getY() - par2.getY(), par1.getZ() - par2.getZ());
	}

	public static Vector3 add(Vector3 par1, Vector3 par2)
	{
		return new Vector3(par1.getX() + par2.getX(), par1.getY() + par2.getY(), par1.getZ() + par2.getZ());
	}

	public static Vector3 add(Vector3 par1, double par2)
	{
		return new Vector3(par1.getX() + par2, par1.getY() + par2, par1.getZ() + par2);
	}

	public static Vector3 multiply(Vector3 vec1, Vector3 vec2)
	{
		return new Vector3(vec1.getX() * vec2.getX(), vec1.getY() * vec2.getY(), vec1.getZ() * vec2.getZ());
	}

	public static Vector3 multiply(Vector3 vec1, double vec2)
	{
		return new Vector3(vec1.getX() * vec2, vec1.getY() * vec2, vec1.getZ() * vec2);
	}

	public static Vector3 readFromNBT(String prefix, NBTTagCompound par1NBTTagCompound)
	{
		return new Vector3(par1NBTTagCompound.getDouble(prefix + "X"), par1NBTTagCompound.getDouble(prefix + "Y"), par1NBTTagCompound.getDouble(prefix + "Z"));
	}

	/**
	 * Returns the coordinates as integers
	 */
	@Override
	public int intX()
	{
		return (int) Math.floor(x);
	}

	@Override
	public int intY()
	{
		return (int) Math.floor(y);
	}

	public int intZ()
	{
		return (int) Math.floor(z);
	}

	public boolean isEquals(Vector3 vector)
	{
		return x == vector.getX() && y == vector.getY() && z == vector.getZ();
	}

	/**
	 * Makes a new copy of this Vector. Prevents variable referencing problems.
	 */
	@Override
	public Vector3 clone()
	{
		return new Vector3(x, y, z);
	}

	public Block getBlock(IBlockAccess world)
	{
		return world.getBlock(intX(), intY(), intZ());
	}

	public int getBlockMetadata(IBlockAccess world)
	{
		return world.getBlockMetadata(intX(), intY(), intZ());
	}

	public TileEntity getTileEntity(IBlockAccess world)
	{
		return world.getTileEntity(intX(), intY(), intZ());
	}

	public void setBlock(World world, Block block, int metadata)
	{
		world.setBlock(intX(), intY(), intZ(), block, metadata, 1);
	}

	public void setBlock(World world, Block block)
	{
		world.setBlock(intX(), intY(), intZ(), block);
	}

	public void setBlockWithNotify(World world, Block block, int metadata)
	{
		world.setBlock(intX(), intY(), intZ(), block, metadata, 1);
	}

	/**
	 * Converts this Vector3 into a Vector2 by dropping the Y axis.
	 */
	public Vector2 toVector2()
	{
		return new Vector2(x, z);
	}

	/**
	 * Converts this vector three into a Minecraft Vec3 object
	 */
	public Vec3 toVec3()
	{
		return Vec3.createVectorHelper(x, y, z);
	}

	/**
	 * Compares two vectors and see if they are equal. True if so.
	 */
	public boolean isEqual(Vector3 vector3)
	{
		return x == vector3.getX() && y == vector3.getY() && z == vector3.getZ();
	}

	public double distanceTo(Vector3 vector3)
	{
		double var2 = vector3.getX() - x;
		double var4 = vector3.getY() - y;
		double var6 = vector3.getZ() - z;
		return MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
	}

	public void add(Vector3 par1)
	{
		x += par1.getX();
		y += par1.getY();
		z += par1.getZ();
	}

	@Override
	public void add(double par1)
	{
		x += par1;
		y += par1;
		z += par1;
	}

	public void subtract(Vector3 amount)
	{
		x -= amount.getX();
		y -= amount.getY();
		z -= amount.getZ();
	}

	@Override
	public void multiply(double amount)
	{
		x *= amount;
		y *= amount;
		z *= amount;
	}

	public void multiply(Vector3 vec)
	{
		x *= vec.getX();
		y *= vec.getY();
		z *= vec.getZ();
	}

	/**
	 * Saves this Vector3 to disk
	 *
	 * @param prefix
	 *            - The prefix of this save. Use some unique string.
	 * @param par1NBTTagCompound
	 *            - The NBT compound object to save the data in
	 */
	public void writeToNBT(String prefix, NBTTagCompound par1NBTTagCompound)
	{
		par1NBTTagCompound.setDouble(prefix + "X", x);
		par1NBTTagCompound.setDouble(prefix + "Y", y);
		par1NBTTagCompound.setDouble(prefix + "Z", z);
	}

	@Override
	public Vector3 round()
	{
		return new Vector3(Math.round(x), Math.round(y), Math.round(z));
	}

	@Override
	public Vector3 floor()
	{
		return new Vector3(Math.floor(x), Math.floor(y), Math.floor(z));
	}

	/**
	 * Gets all entities inside of this position in block space.
	 */

	@SuppressWarnings("unchecked")
	public List<Entity> getEntitiesWithin(World worldObj, Class<? extends Entity> par1Class)
	{
		return worldObj.getEntitiesWithinAABB(par1Class, AxisAlignedBB.getBoundingBox(intX(), intY(), intZ(), intX() + 1, intY() + 1, intZ() + 1));
	}

	@Override
	public String toString()
	{
		return "Vector3 [" + x + "," + y + "," + z + "]";
	}
}
