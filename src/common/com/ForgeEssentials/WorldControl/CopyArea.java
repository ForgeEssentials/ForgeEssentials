package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;

/**
 * @author UnknownCoder : Max Bruce Defines an area to be
 */

public class CopyArea extends AreaBase
{
	public Point offset;
	public boolean flipX = false;
	public boolean flipY = false;
	public boolean flipZ = false;
	public boolean rotateX = false;
	public boolean rotateY = false;
	public boolean rotateZ = false;
	
	// yzx form.  list of all the blocks.
	private List<BlueprintBlock> area = new ArrayList<BlueprintBlock>();
	
	public CopyArea(EntityPlayer player, Point start, Point end)
	{
		super(start, end);
		this.alignPoints();
		build(player.worldObj);
	}
	
	public CopyArea(EntityPlayer player, Selection selection)
	{
		super(selection.start, selection.end);
		this.alignPoints();
		build(player.worldObj);
	}
	
	private void build(World world)
	{
		for (int y = start.y; y < end.y; y++)
			for (int z = start.z; z < end.z; z++)
				for (int x = start.x; x < end.x; x++)
					addBlock(x, y, z, world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z));
	}
	
	public void clear()
	{
		area.clear();
	}

	public void addBlock(int x, int y, int z, int blockID, int metadata)
	{
		addBlock(new BlueprintBlock(x, y, z, blockID, metadata));
	}
	
	public void addBlock(BlueprintBlock block)
	{
		if (area.contains(block) || block.isAir())
			return;
		area.add(block);
	}

	public void loadArea(EntityPlayer sender, BackupArea back, boolean clear)
	{
		int playerX = MathHelper.floor_double(sender.posX);
		int playerY = MathHelper.floor_double(sender.posY);
		int playerZ = MathHelper.floor_double(sender.posZ);

		for (BlueprintBlock afterBlock: area)
		{
			int oldOffsetX = afterBlock.x - start.x;
			int oldOffsetY = afterBlock.y - start.y;
			int oldOffsetZ = afterBlock.z - start.z;
			int oX = offset.x;
			int oY = offset.y;
			int oZ = offset.z;

			if (flipX)
			{
				oldOffsetX = afterBlock.x - end.x - oldOffsetX;
				oX = afterBlock.x - end.x - oX;
			}

			if (flipY)
			{
				oldOffsetY = afterBlock.y - end.y - oldOffsetY;
				oY = afterBlock.y - end.y - oY;
			}

			if (flipZ)
			{
				oldOffsetZ = afterBlock.z - end.z - oldOffsetZ;
				oZ = afterBlock.z - end.z - oZ;
			}

			if (rotateX)
			{
				oldOffsetX = -oldOffsetX;
				oX = -oX;
			}

			if (rotateY)
			{
				oldOffsetY = -oldOffsetY;
				oY = -oY;
			}

			if (rotateZ)
			{
				oldOffsetZ = -oldOffsetZ;
				oZ = -oZ;
			}

			int x = playerX + oldOffsetX - oX;
			int y = playerY + oldOffsetY - oY;
			int z = playerZ + oldOffsetZ - oZ;
			int blockID = sender.worldObj.getBlockId(x, y, z);
			int metadata = sender.worldObj.getBlockMetadata(x, y, z);
			
			if (!afterBlock.isAir() || clear)
			{
				back.addBlockBefore(x, y, z, blockID, metadata);
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, afterBlock.blockID, afterBlock.metadata);
				back.addBlockAfter(x, y, z, afterBlock.blockID, afterBlock.metadata);
			}
		}
	}

	public void loadAreaStack(EntityPlayer sender, BackupArea back, boolean clear, int sX, int sY, int sZ)
	{
		for (int i = 0; i < area.size(); i++)
		{
			BlueprintBlock obj = area.get(i);
			int offX = obj.x - start.x;
			int offY = obj.y - start.y;
			int offZ = obj.z - start.z;

			if (flipX)
				offX = obj.x - end.x - offX;

			if (flipY)
				offY = obj.y - end.y - offY;

			if (flipZ)
				offZ = obj.z - end.z - offZ;

			if (rotateX)
				offX = -offX;

			if (rotateY)
				offY = -offY;

			if (rotateZ)
				offZ = -offZ;

			int x = offX + sX - offset.x;
			int y = offY + sY - offset.y;
			int z = offZ + sZ - offset.z;
			int blockID = sender.worldObj.getBlockId(x, y, z);
			int metadata = sender.worldObj.getBlockMetadata(x, y, z);

			boolean good = true;

			if (obj.blockID == 0 && !clear)
				good = false;

			if (good)
			{
				back.addBlockBefore(x, y, z, blockID, metadata);
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
				back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}

	public void loadAreaMove(EntityPlayer sender, BackupArea back, boolean clear, int sX, int sY, int sZ)
	{
		for (int i = 0; i < area.size(); i++)
		{
			BlueprintBlock obj = area.get(i);
			int x = obj.x - start.x + sX;
			int y = obj.y - start.y + sY;
			int z = obj.z - start.z + sZ;

			boolean good = true;
			if (obj.blockID == 0 && !clear)
				good = false;

			if (good)
			{
				back.addBlockBefore(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z));
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
				back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}
}
