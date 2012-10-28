package com.ForgeEssentials.WorldControl;

import java.io.Serializable;

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
}
