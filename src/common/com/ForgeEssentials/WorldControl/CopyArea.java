package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.List;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;

/**
 * @author UnknownCoder : Max Bruce Defines an area to be
 */

public class CopyArea extends AreaBase
{
	private List<BlueprintBlock> area = new ArrayList<BlueprintBlock>();
	public String username;
	public int worldEdit;
	public Point offset;
	public int id = 0;
	public int copyDir = 0;
	public boolean flipX = false;
	public boolean flipY = false;
	public boolean flipZ = false;
	public boolean rotateX = false;
	public boolean rotateY = false;
	public boolean rotateZ = false;

	public int getXLength()
	{
		return Math.abs(end.x - start.x) + 1;
	}

	public int getZLength()
	{
		return Math.abs(end.z - start.z) + 1;
	}

	public CopyArea(String user, int id, int worldEdit)
	{
		username = user;
		this.worldEdit = worldEdit;
		this.id = id;
	}

	public void clear()
	{
		area.clear();
	}

	public void addBlock(int x, int y, int z, int blockID, int metadata)
	{
		area.add(new BlueprintBlock(x, y, z, blockID, metadata));
	}

	public void setOffset(int x, int y, int z)
	{
		offset = new Point(x, y, z);
	}

	public void loadArea(EntityPlayer sender, BackupArea back, boolean clear)
	{
		int playerX = MathHelper.floor_double(sender.posX);
		int playerY = MathHelper.floor_double(sender.posY);
		int playerZ = MathHelper.floor_double(sender.posZ);

		for (int i = 0; i < area.size(); i++)
		{
			BlueprintBlock afterBlock = area.get(i);
			int oldOffsetX = afterBlock.x - start.x;
			int oldOffsetY = afterBlock.y - start.y;
			int oldOffsetZ = afterBlock.z - start.z;
			int oX = this.offset.x;
			int oY = this.offset.y;
			int oZ = this.offset.z;

			if (flipX)
			{
				oldOffsetX = (afterBlock.x - end.x) - oldOffsetX;
				oX = (afterBlock.x - end.x) - oX;
			}

			if (flipY)
			{
				oldOffsetY = (afterBlock.y - end.y) - oldOffsetY;
				oY = (afterBlock.y - end.y) - oY;
			}

			if (flipZ)
			{
				oldOffsetZ = (afterBlock.z - end.z) - oldOffsetZ;
				oZ = (afterBlock.z - end.z) - oZ;
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

			boolean good = true;

			if (afterBlock.blockID == 0 && !clear)
				good = false;

			if (good)
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
			{
				offX = (obj.x - end.x) - offX;
			}

			if (flipY)
			{
				offY = (obj.y - end.y) - offY;
			}

			if (flipZ)
			{
				offZ = (obj.z - end.z) - offZ;
			}

			if (rotateX)
			{
				offX = -offX;
			}

			if (rotateY)
			{
				offY = -offY;
			}

			if (rotateZ)
			{
				offZ = -offZ;
			}

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
