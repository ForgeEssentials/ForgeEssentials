package com.ForgeEssentials.WorldControl;

import java.io.Serializable;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * @author UnknownCoder : Max Bruce A class for Blocks that can be read/written from a binary file hence serializable.
 */
public class BlueprintBlock implements Serializable
{
	int x = 0;
	int y = 0;
	int z = 0;
	int blockID = 0;
	int metadata = 0;
	NBTTagCompound tileEntity;

	public BlueprintBlock(int X, int Y, int Z, int bid, int meta, TileEntity te)
	{
		x = X;
		y = Y;
		z = Z;
		blockID = bid;
		metadata = meta;
		if (te != null)
		{
			tileEntity = new NBTTagCompound();
			te.writeToNBT(tileEntity);
		}
	}
	
	public BlueprintBlock(int X, int Y, int Z, int bid, int meta, NBTTagCompound te)
	{
		x = X;
		y = Y;
		z = Z;
		blockID = bid;
		metadata = meta;
		tileEntity = te;
	}
	
	public BlueprintBlock(int X, int Y, int Z, int bid, int meta)
	{
		x = X;
		y = Y;
		z = Z;
		blockID = bid;
		metadata = meta;
		tileEntity = null;
	}
	
	public static BlueprintBlock loadFromWorld(World world, int x, int y, int z)
	{
		return new BlueprintBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z), world.getBlockTileEntity(x, y, z));
	}

	public boolean isAir()
	{
		return blockID == 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof BlueprintBlock))
			return false;
		else
			return equals((BlueprintBlock) obj);
	}

	public boolean equals(BlueprintBlock block)
	{
		if (this == block)
			return true;
		
		boolean works = x == block.x && y == block.y && z == block.z && blockID == block.blockID && metadata == block.metadata;
		
		if (!works)
			return false;
		
		if (tileEntity == null)
			return block.tileEntity == null;
		
		return tileEntity.equals(block.tileEntity);
	}

	public void setInWorld(World world)
	{
		world.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata);
		if (tileEntity != null)
			world.getBlockTileEntity(x, y, z).readFromNBT(tileEntity);
	}
}
