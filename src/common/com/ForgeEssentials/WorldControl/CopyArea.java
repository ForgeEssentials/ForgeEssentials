package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

/**
 * @author UnknownCoder : Max Bruce Defines an area to be
 */

public class CopyArea extends AreaBase
{
	public boolean flipX = false;
	public boolean flipY = false;
	public boolean flipZ = false;
	public boolean rotateX = false;
	public boolean rotateY = false;
	public boolean rotateZ = false;

	// yzx form. list of all the blocks.
	private List<BlueprintBlock> area = new ArrayList<BlueprintBlock>();

	public CopyArea(EntityPlayer player, Point start, Point end)
	{
		super(start, end);
		build(player.worldObj);
	}

	public CopyArea(EntityPlayer player, Selection selection)
	{
		super(selection.start, selection.end);
		build(player.worldObj);
	}

	private void build(World world)
	{
		Point[] alligned = getAlignedPoints(start, end);
		int offsetsX =  alligned[0].x;
		int offsetsY =  alligned[0].y;
		int offsetsZ =  alligned[0].z;
		
		for (int y = 0; y < this.getYLength(); y++)
			for (int z = 0; z < this.getZLength(); z++)
				for (int x = 0; x < this.getXLength(); x++)
					addBlock(x+offsetsX, y+offsetsY, z+offsetsZ, world.getBlockId(x+offsetsX, y+offsetsY, z+offsetsZ), world.getBlockMetadata(x+offsetsX, y+offsetsY, z+offsetsZ), world.getBlockTileEntity(x+offsetsX, y+offsetsY, z+offsetsZ));
	}

	public void clear()
	{
		area.clear();
	}

	public void addBlock(int x, int y, int z, int blockID, int metadata, TileEntity te)
	{
		addBlock(new BlueprintBlock(x, y, z, blockID, metadata, te));
	}

	public void addBlock(BlueprintBlock block)
	{
		if (area.contains(block) || block.isAir())
			return;
		area.add(block);
	}

	public void loadArea(EntityPlayer sender, Point loadStart, BackupArea back, boolean clear)
	{
		for (BlueprintBlock afterBlock : area)
		{
			int distFromStartX = afterBlock.x - start.x;
			int distFromStartY = afterBlock.y - start.y;
			int distFromStartZ = afterBlock.z - start.z;
			int x = loadStart.x + distFromStartX;
			int y = loadStart.x + distFromStartY;
			int z = loadStart.x + distFromStartZ;

			if (!afterBlock.isAir() || clear)
			{
				back.addBlockBefore(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z), sender.worldObj.getBlockTileEntity(x, y, z));
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, afterBlock.blockID, afterBlock.metadata);
				sender.worldObj.setBlockTileEntity(x, y, z, afterBlock.tileEntity);
				back.addBlockAfter(x, y, z, afterBlock.blockID, afterBlock.metadata, afterBlock.tileEntity);
			}
		}
	}
}
