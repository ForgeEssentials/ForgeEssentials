package com.ForgeEssentials.WorldControl;

import java.io.Serializable;

import net.minecraft.src.World;

/**
 * @author UnknownCoder : Max Bruce
 * A class for Blocks that can be read/written from a binary file hence serializable.
 */
public class BlueprintBlock implements Serializable
{
	int x = 0;
	int y = 0;
	int z = 0;
	int blockID = 0;
	int metadata = 0;

	public BlueprintBlock(int X, int Y, int Z, int bid, int meta)
	{
		x = X;
		y = Y;
		z = Z;
		blockID = bid;
		metadata = meta;
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
			return equals((BlueprintBlock)obj);
	}
	
	public boolean equals(BlueprintBlock block)
	{
		if (x == block.x && y == block.y && z == block.z && blockID == block.blockID && metadata == block.metadata)
			return true;
		
		return false;
	}
	
	public void setInWorld(World world)
	{
		world.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata);
	}
}
