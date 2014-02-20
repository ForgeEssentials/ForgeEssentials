package com.forgeessentials.afterlife;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntitySkull;

import com.forgeessentials.util.AreaSelector.WorldPoint;

public class FEskullTe extends TileEntitySkull
{
	@Override
	public void invalidate()
	{
		super.invalidate();
		
		WorldPoint point = new WorldPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord);
		Grave grave = ModuleAfterlife.instance.deathchest.gravemap.get(point.toString());
		if (grave == null)
			return;
		if (grave.protEnable)
		{
			this.worldObj.setBlock(point.x, point.y, point.z, Block.skull.blockID, 1, 1);
			FEskullTe te = new FEskullTe();
			te.setSkullType(3, grave.owner);
			this.worldObj.setBlockTileEntity(point.x, point.y, point.z, te);
		}
		else
		{
			ModuleAfterlife.instance.deathchest.removeGrave(grave, true);
		}
	}
}
