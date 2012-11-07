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

	public CopyArea(World world, Point start, Point end)
	{
		super(start, end);
		build(world);
	}

	public CopyArea(World world, Selection selection)
	{
		super(selection.start, selection.end);
		build(world);
	}

	private void build(World world)
	{
		Point[] alligned = getAlignedPoints(start, end);
		int offsetsX =  alligned[0].x;
		int offsetsY =  alligned[0].y;
		int offsetsZ =  alligned[0].z;
		
		for (int y = 0; y <= start.y - end.y; y++)
			for (int z = 0; z <= start.z - end.z; z++)
				for (int x = 0; x <= start.x - end.x; x++)
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

	public void outputArea(World world, Point loadStart, BackupArea back, boolean clear)
	{
		for (BlueprintBlock block : area)
		{
			int x = block.x + (start.x - loadStart.x);
			int y = block.y + (start.y - loadStart.y);
			int z = block.z + (start.z - loadStart.x);

			if (!block.isAir() || clear)
			{
				back.addBlockBefore(new BlueprintBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z), world.getBlockTileEntity(x, y, z)));
				block.setInWorld(world);
				back.addBlockBefore(new BlueprintBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z), world.getBlockTileEntity(x, y, z)));
			}
		}
	}
}
