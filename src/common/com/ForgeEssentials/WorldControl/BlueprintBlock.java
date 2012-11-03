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
	TileEntity tileEntity;

	public BlueprintBlock(int X, int Y, int Z, int bid, int meta, TileEntity te)
	{
		x = X;
		y = Y;
		z = Z;
		blockID = bid;
		metadata = meta;
		tileEntity = te;
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
		// TODO: Abrar has to make this comparison of TE's work using their NBT or some mumbo jumbo like that
		return x == block.x && y == block.y && z == block.z && blockID == block.blockID && metadata == block.metadata && tileEntitiesSame(tileEntity, block.tileEntity);
	}
	
	private boolean tileEntitiesSame(TileEntity te1, TileEntity te2)
	{
		if (te1 == te2)
			return true;
		
		NBTTagCompound tag1 = new NBTTagCompound();
		NBTTagCompound tag2 = new NBTTagCompound();
		
		te1.writeToNBT(tag1);
		te2.writeToNBT(tag2);
		
		return tag1.equals(tag2);
	}

	public void setInWorld(World world)
	{
		world.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata);
	}
}
