package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;

/**
 * @author UnknownCoder : Max Bruce
 * Defines an area to be 
 */

public class CopyArea {
	private List<BlueprintBlock> area = new ArrayList<BlueprintBlock>();
	public String username;
	public int worldEdit;
	public int startX = 0;
	public int startY = 0;
	public int startZ = 0;
	public int endX = 0;
	public int endY = 0;
	public int endZ = 0;
	public int id = 0;
	public int offsetX = 0;
	public int offsetY = 0;
	public int offsetZ = 0;
	public int copyDir = 0;
	public boolean flipX = false;
	public boolean flipY = false;
	public boolean flipZ = false;
	public boolean rotateX = false;
	public boolean rotateY = false;
	public boolean rotateZ = false;
	
	public int getXLength() {
		return Math.abs(endX - startX) + 1;
	}
	
	public int getZLength() {
		return Math.abs(endZ - startZ) + 1;
	}
	
	public CopyArea(String user, int id, int worldEdit) {
		username=user;
		this.worldEdit=worldEdit;
		this.id=id;
	}
	
	public void clear() {
		area.clear();
	}
	
	public void addBlock(int x, int y, int z, int blockID, int metadata) {
		area.add(new BlueprintBlock(x, y, z, blockID, metadata));
	}
	
	public void setOffset(int x, int y, int z) {
		offsetX = x;
		offsetY = y;
		offsetZ = z;
	}
	
	public void loadArea(EntityPlayer sender, BackupArea back, boolean clear) {
		int playerX = MathHelper.floor_double(sender.posX);
		int playerY = MathHelper.floor_double(sender.posY);
		int playerZ = MathHelper.floor_double(sender.posZ);
		
		for(int i = 0; i < area.size(); i++) {
			BlueprintBlock afterBlock = area.get(i);
			int oldOffsetX = afterBlock.x - startX;
			int oldOffsetY = afterBlock.y - startY;
			int oldOffsetZ = afterBlock.z - startZ;
			int oX = this.offsetX;
			int oY = this.offsetY;
			int oZ = this.offsetZ;
			
			if(flipX) {
				oldOffsetX = (afterBlock.x - endX) - oldOffsetX;
				oX = (afterBlock.x - endX) - oX;
			}
			
			if(flipY) {
				oldOffsetY = (afterBlock.y - endY) - oldOffsetY;
				oY = (afterBlock.y - endY) - oY;
			}
			
			if(flipZ) {
				oldOffsetZ = (afterBlock.z - endZ) - oldOffsetZ;
				oZ = (afterBlock.z - endZ) - oZ;
			}
			
			if(rotateX) {
				oldOffsetX = -oldOffsetX;
				oX = -oX;
			}
			
			if(rotateY) {
				oldOffsetY = -oldOffsetY;
				oY = -oY;
			}
			
			if(rotateZ) {
				oldOffsetZ = -oldOffsetZ;
				oZ = -oZ;
			}
			
			int x = playerX + oldOffsetX - oX;
			int y = playerY + oldOffsetY - oY;
			int z = playerZ + oldOffsetZ - oZ;
			int blockID = sender.worldObj.getBlockId(x, y, z);
			int metadata = sender.worldObj.getBlockMetadata(x, y, z);
			
			boolean good = true;
			
			if(afterBlock.blockID == 0 && !clear) 
				good = false;
			
			if(good) {
				back.addBlockBefore(x, y, z, blockID, metadata);
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, afterBlock.blockID, afterBlock.metadata);
				back.addBlockAfter(x, y, z, afterBlock.blockID, afterBlock.metadata);
			}
		}
	}
	
	public void loadAreaStack(EntityPlayer sender, BackupArea back, boolean clear, int sX, int sY, int sZ) {
		for(int i = 0;i<area.size();i++) {
			BlueprintBlock obj = area.get(i);
			int offX = obj.x - startX;
			int offY = obj.y - startY;
			int offZ = obj.z - startZ;
			
			if(flipX) {
				offX = (obj.x - endX) - offX;
			}
			
			if(flipY) {
				offY = (obj.y - endY) - offY;
			}
			
			if(flipZ) {
				offZ = (obj.z - endZ) - offZ;
			}
			
			if(rotateX) {
				offX = -offX;
			}
			
			if(rotateY) {
				offY = -offY;
			}
			
			if(rotateZ) {
				offZ = -offZ;
			}
			
			int x = offX + sX - offsetX;
			int y = offY + sY - offsetY;
			int z = offZ + sZ - offsetZ;
			int blockID = sender.worldObj.getBlockId(x, y, z);
			int metadata = sender.worldObj.getBlockMetadata(x, y, z);
			
			boolean good = true;
			
			if(obj.blockID == 0 && !clear) 
				good = false;
			
			if(good) {
				back.addBlockBefore(x, y, z, blockID, metadata);
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
				back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}
	
	public void loadAreaMove(EntityPlayer sender, BackupArea back, boolean clear, int sX, int sY, int sZ) {
		for(int i = 0; i < area.size(); i++) {
			BlueprintBlock obj = area.get(i);
			int x = obj.x - startX + sX;
			int y = obj.y - startY + sY;
			int z = obj.z - startZ + sZ;
			
			boolean good = true;
			if(obj.blockID == 0 && !clear)
				good = false;
			
			if(good) {
				back.addBlockBefore(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z));
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
				back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}
}
