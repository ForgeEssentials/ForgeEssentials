package com.ForgeEssentials.WorldControl.commands;

import java.io.Serializable;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class SaveableBlock implements Serializable
{
	private int				x;
	private int				y;
	private int				z;
	private short			blockID;
	private byte			metadata;
	private NBTTagCompound	tile;

	/**
	 * generates the block from the world.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public SaveableBlock(World world, int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = (short) world.getBlockId(x, y, z);
		metadata = (byte) world.getBlockMetadata(x, y, z);

		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity != null)
		{
			NBTTagCompound compound = new NBTTagCompound();
			entity.writeToNBT(compound);
			tile = compound;
		}
	}
	
	public boolean equals(Object object)
	{
		if (object instanceof SaveableBlock)
		{
			SaveableBlock block = (SaveableBlock) object;
			return x == block.x && y == block.y && z == block.z && blockID == block.blockID && metadata == block.metadata && tile.equals(block.tile);
		}
		return false;
	}
	
	public void setinWorld(World world)
	{
		world.setBlockAndMetadata(x, y, z, blockID, metadata);
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity != null && tile != null)
			entity.readFromNBT(tile);
	}
}
