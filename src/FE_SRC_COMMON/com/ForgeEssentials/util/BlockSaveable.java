package com.ForgeEssentials.util;

//Depreciated
import java.io.Serializable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSaveable implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7134842755892862662L;
	private int					x;
	private int					y;
	private int					z;
	private short				blockID;
	private byte				metadata;
	private NBTTagCompound		tile;

	/**
	 * generates the block from the world.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public BlockSaveable(World world, int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		blockID = (short) world.getBlockId(x, y, z);
		metadata = (byte) world.getBlockMetadata(x, y, z);

		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity != null)
		{
			try
			{
				NBTTagCompound compound = new NBTTagCompound();
				entity.writeToNBT(compound);
				tile = compound;
			}
			catch (Exception e)
			{
			}
		}
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof BlockSaveable)
		{
			BlockSaveable block = (BlockSaveable) object;
			return x == block.x && y == block.y && z == block.z && blockID == block.blockID && metadata == block.metadata && tile.equals(block.tile);
		}
		return false;
	}

	/**
	 * @param world
	 * @return if the block was actually set.
	 */
	public boolean setinWorld(World world)
	{
		if (equals(new BlockSaveable(world, x, y, z)))
			return false;

		world.setBlockAndMetadata(x, y, z, blockID, metadata);
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity != null && tile != null)
		{
			entity.readFromNBT(tile);
		}

		return true;
	}

	public boolean isAir()
	{
		return blockID == 0;
	}
}
