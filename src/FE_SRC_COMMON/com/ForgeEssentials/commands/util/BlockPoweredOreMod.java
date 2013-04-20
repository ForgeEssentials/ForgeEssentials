package mod.fml.admintools;

import java.util.Random;

import net.minecraft.block.BlockOreStorage;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPoweredOreMod extends BlockOreStorage
{
	public boolean isPulsed = false;
    public int previousBlockID = 0;
    public int strength = 15;
    public Object previusEntity = null;
    public int previusMetaData = 0;

    public BlockPoweredOreMod(int par1)
    {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return isPulsed? this.strength:15;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World var1, int var2, int var3, int var4, Random var5)
    {
        if (this.isPulsed)
        {
            var1.setBlock(var2, var3, var4, this.previousBlockID, this.previusMetaData, 3);
            var1.setBlockMetadataWithNotify(var2, var3, var4, this.previusMetaData, 2);

            if (this.previusEntity != null)
            {
            	((TileEntity)this.previusEntity).validate();
                ((TileEntity)this.previusEntity).blockMetadata = this.previusMetaData;
                var1.setBlockTileEntity(var2, var3, var4, (TileEntity)this.previusEntity);
            }
        }
    }
}
