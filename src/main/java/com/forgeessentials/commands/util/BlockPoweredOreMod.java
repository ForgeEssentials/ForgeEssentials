package com.forgeessentials.commands.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPoweredOreMod extends BlockCompressedPowered {
    public boolean isPulsed = false;
    public Block previousBlock = Blocks.air;
    public int strength = 15;
    public Object previusEntity = null;
    public int previusMetaData = 0;

    public BlockPoweredOreMod()
    {
        super(MapColor.tntColor);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    @Override
	public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
	public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return isPulsed ? this.strength : 15;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
	public void updateTick(World var1, int var2, int var3, int var4, Random var5)
    {
        if (this.isPulsed)
        {
            var1.setBlock(var2, var3, var4, this.previousBlock, this.previusMetaData, 3);
            var1.setBlockMetadataWithNotify(var2, var3, var4, this.previusMetaData, 2);

            if (this.previusEntity != null)
            {
                ((TileEntity) this.previusEntity).validate();
                ((TileEntity) this.previusEntity).blockMetadata = this.previusMetaData;
                var1.setTileEntity(var2, var3, var4, (TileEntity) this.previusEntity);
            }
        }
    }
}
