package com.ForgeEssentials.util.vector;

import java.util.List;

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

public class Vector3 extends Vector2 implements Cloneable
{
	public double	z;

	public Vector3()
	{
		this(0, 0, 0);
	}

	public Vector3(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
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
		return x == vector.x && y == vector.y && z == vector.z;
	}

	/**
	 * Makes a new copy of this Vector. Prevents variable referencing problems.
	 */
	@Override
	public Vector3 clone()
	{
		return new Vector3(x, y, z);
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

	public int getBlockID(IBlockAccess world)
	{
		return world.getBlockId(intX(), intY(), intZ());
	}

	public int getBlockMetadata(IBlockAccess world)
	{
		return world.getBlockMetadata(intX(), intY(), intZ());
	}

	public TileEntity getTileEntity(IBlockAccess world)
	{
		return world.getBlockTileEntity(intX(), intY(), intZ());
	}

	public void setBlock(World world, int id, int metadata)
	{
		world.setBlockAndMetadataWithNotify(intX(), intY(), intZ(), id, metadata, 1);
	}

	public void setBlock(World world, int id)
	{
		world.func_94575_c(intX(), intY(), intZ(), id);
	}

	public void setBlockWithNotify(World world, int id, int metadata)
	{
		world.setBlockAndMetadataWithNotify(intX(), intY(), intZ(), id, metadata, 1);
	}

	public void setBlockWithNotify(World world, int id)
	{
		world.setBlockWithNotify(intX(), intY(), intZ(), id);
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
		return x == vector3.x && y == vector3.y && z == vector3.z;
	}

	/**
	 * Gets the distance between two vectors
	 * @return The distance
	 */
	public static double distance(Vector3 par1, Vector3 par2)
	{
		double var2 = par1.x - par2.x;
		double var4 = par1.y - par2.y;
		double var6 = par1.z - par2.z;
		return MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
	}

	public double distanceTo(Vector3 vector3)
	{
		double var2 = vector3.x - x;
		double var4 = vector3.y - y;
		double var6 = vector3.z - z;
		return MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
	}

	public static Vector3 subtract(Vector3 par1, Vector3 par2)
	{
		return new Vector3(par1.x - par2.x, par1.y - par2.y, par1.z - par2.z);
	}

	public static Vector3 add(Vector3 par1, Vector3 par2)
	{
		return new Vector3(par1.x + par2.x, par1.y + par2.y, par1.z + par2.z);
	}

	public static Vector3 add(Vector3 par1, double par2)
	{
		return new Vector3(par1.x + par2, par1.y + par2, par1.z + par2);
	}

	public void add(Vector3 par1)
	{
		x += par1.x;
		y += par1.y;
		z += par1.z;
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
		x -= amount.x;
		y -= amount.y;
		z -= amount.z;
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
		x *= vec.x;
		y *= vec.y;
		z *= vec.z;
	}

	public static Vector3 multiply(Vector3 vec1, Vector3 vec2)
	{
		return new Vector3(vec1.x * vec2.x, vec1.y * vec2.y, vec1.z * vec2.z);
	}

	public static Vector3 multiply(Vector3 vec1, double vec2)
	{
		return new Vector3(vec1.x * vec2, vec1.y * vec2, vec1.z * vec2);
	}

	public static Vector3 readFromNBT(String prefix, NBTTagCompound par1NBTTagCompound)
	{
		Vector3 tempVector = new Vector3();
		tempVector.x = par1NBTTagCompound.getDouble(prefix + "X");
		tempVector.y = par1NBTTagCompound.getDouble(prefix + "Y");
		tempVector.z = par1NBTTagCompound.getDouble(prefix + "Z");
		return tempVector;
	}

	/**
	 * Saves this Vector3 to disk
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
