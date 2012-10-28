package com.ForgeEssentials.WorldControl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public class CopyArea {
	private List<BlueprintBlock> area = new ArrayList<BlueprintBlock>();
	public String username;
	public int worldEdit;
	public int startX=0;
	public int startY=0;
	public int startZ=0;
	public int endX=0;
	public int endY=0;
	public int id = 0;
	public int endZ=0;
	public int oX=0;
	public int oY=0;
	public int oZ=0;
	public int copyDir = 0;
	public boolean flipX = false;
	public boolean flipY = false;
	public boolean flipZ = false;
	public boolean rotateX = false;
	public boolean rotateY = false;
	public boolean rotateZ = false;
	
	public int getXLength() {
		return Math.abs(endX-startX)+1;
	}
	
	public int getZLength() {
		return Math.abs(endZ-startZ)+1;
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
		oX=x;
		oY=y;
		oZ=z;
	}
	
	public void loadArea(EntityPlayer sender, BackupArea back, boolean clear) {
		int plrX = MathHelper.floor_double(sender.posX);
		int plrY = MathHelper.floor_double(sender.posY);
		int plrZ = MathHelper.floor_double(sender.posZ);
		for(int i = 0;i<area.size();i++) {
			BlueprintBlock obj = area.get(i);
			int offX = obj.x-startX;
			int offY = obj.y-startY;
			int offZ = obj.z-startZ;
			int oX = this.oX;
			int oY = this.oY;
			int oZ = this.oZ;
			if(flipX) {
				offX = (obj.x-endX)-offX;
				oX = (obj.x-endX)-oX;
			}
			if(flipY) {
				offY = (obj.y-endY)-offY;
				oY = (obj.y-endY)-oY;
			}
			if(flipZ) {
				offZ = (obj.z-endZ)-offZ;
				oZ = (obj.z-endZ)-oZ;
			}
			if(rotateX) {
				offX = -offX;
				oX = -oX;
			}
			if(rotateY) {
				offY = -offY;
				oY = -oY;
			}
			if(rotateZ) {
				offZ = -offZ;
				oZ = -oZ;
			}
			int x = plrX+offX-oX;
			int y = plrY+offY-oY;
			int z = plrZ+offZ-oZ;
			int bid = sender.worldObj.getBlockId(x, y, z);
			int meta = sender.worldObj.getBlockMetadata(x, y, z);
			boolean good=true;
			if(obj.blockID==0&&!clear)good=false;
			if(good) {
			back.addBlockBefore(x, y, z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
			back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}
	
	public void loadAreaStack(EntityPlayer sender, BackupArea back, boolean clear, int sX, int sY, int sZ) {
		for(int i = 0;i<area.size();i++) {
			BlueprintBlock obj = area.get(i);
			int offX = obj.x-startX;
			int offY = obj.y-startY;
			int offZ = obj.z-startZ;
			if(flipX) {
				offX = (obj.x-endX)-offX;
			}
			if(flipY) {
				offY = (obj.y-endY)-offY;
			}
			if(flipZ) {
				offZ = (obj.z-endZ)-offZ;
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
			int x = offX+sX-oX;
			int y = offY+sY-oY;
			int z = offZ+sZ-oZ;
			int bid = sender.worldObj.getBlockId(x, y, z);
			int meta = sender.worldObj.getBlockMetadata(x, y, z);
			boolean good=true;
			if(obj.blockID==0&&!clear)good=false;
			if(good) {
			back.addBlockBefore(x, y, z, bid, meta);
			sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
			back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}
	
	public void loadAreaMove(EntityPlayer sender, BackupArea back, boolean clear, int sX, int sY, int sZ) {
		for(int i = 0;i<area.size();i++) {
			BlueprintBlock obj = area.get(i);
			int x = obj.x-startX+sX;
			int y = obj.y-startY+sY;
			int z = obj.z-startZ+sZ;
			boolean good=true;
			if(obj.blockID==0&&!clear)good=false;
			if(good) {
				back.addBlockBefore(x, y, z, sender.worldObj.getBlockId(x, y, z), sender.worldObj.getBlockMetadata(x, y, z));
				sender.worldObj.setBlockAndMetadataWithNotify(x, y, z, obj.blockID, obj.metadata);
				back.addBlockAfter(x, y, z, obj.blockID, obj.metadata);
			}
		}
	}
}
