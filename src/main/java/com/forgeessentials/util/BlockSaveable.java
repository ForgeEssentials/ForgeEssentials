package com.forgeessentials.util;

//Depreciated

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSaveable {
	
    private int x;
    private int y;
    private int z;
    private Block blockID;
    private byte metadata;
    private NBTTagCompound tile;

    /**
     * generates the block from the world.
     *
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
        blockID = world.getBlock(x, y, z);
        metadata = (byte) world.getBlockMetadata(x, y, z);

        TileEntity entity = world.getTileEntity(x, y, z);
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

        if (object != null && object instanceof BlockSaveable)
        {
            BlockSaveable block = (BlockSaveable) object;

            // check NBT tag for equality
            boolean tileEqual = tile == null ? block.tile == null : tile.equals(block.tile);

            // return full boolean that takes everything into account
            return tileEqual && blockID == block.blockID && metadata == block.metadata;
        }
        else
        {
            return false;
        }
    }

    /**
     * @param world
     * @return if the block was actually set.
     */
    public boolean setinWorld(World world)
    {
        if (equals(new BlockSaveable(world, x, y, z)))
        {
            return false;
        }

        world.setBlock(x, y, z, blockID, metadata, 3);
        TileEntity entity = world.getTileEntity(x, y, z);
        if (entity != null && tile != null)
        {
            entity.readFromNBT(tile);
        }

        return true;
    }
}
