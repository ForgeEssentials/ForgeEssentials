package com.ForgeEssentials.WorldControl;

import java.io.Serializable;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockArray implements Serializable // UC
{
	
	private ArrayList<BlockInfo> blocks = new ArrayList<BlockInfo>();
	public int offX = 0;
	public int offY = 0;
	public int offZ = 0;
	public int sizeX = 0;
	public int sizeY = 0;
	public int sizeZ = 0;
	public boolean isRelative = true;
	
	public BlockArray(int x, int y, int z, boolean relative, int sizeX, int sizeY, int sizeZ) {
		offX = x;
		offY = y;
		offZ = z;
		isRelative = relative;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}
	
	public ArrayList<LoadingBlock> getBlocksToLoad() {
		ArrayList<LoadingBlock> blox = new ArrayList<LoadingBlock>();
		for(BlockInfo block : blocks) {
			if(block instanceof BlockInfoNTEC) {
				BlockInfoNTEC bloxs = (BlockInfoNTEC)block;
				for(int i = 0;i<bloxs.repeat;i++) {
					blox.add(new LoadingBlock(bloxs.id));
				}
			}else if(block instanceof BlockInfoNTEMC){
				BlockInfoNTEMC bloxs = (BlockInfoNTEMC)block;
				for(int i = 0;i<bloxs.repeat;i++) {
					blox.add(new LoadingBlock(bloxs.id, bloxs.meta));
				}
			}else if(block instanceof BlockInfoNTE) {
				blox.add(new LoadingBlock(((BlockInfoNTE)block).id));
			}else if(block instanceof BlockInfoNTEM) {
				blox.add(new LoadingBlock(((BlockInfoNTEM)block).id, ((BlockInfoNTEM)block).meta));
			}else if(block instanceof BlockInfoTE) {
				blox.add(new LoadingBlock(((BlockInfoTE)block).nbt.getShort("bID"), ((BlockInfoTE)block).nbt.getByte("metad"), true, ((BlockInfoTE)block).nbt));
			}else if(block instanceof BlockInfoNTES) {
				BlockInfoNTES bloxs = ((BlockInfoNTES)block);
				blox.add(new LoadingBlock(bloxs.id).addCoords(bloxs.x, bloxs.y, bloxs.z));
			}else if(block instanceof BlockInfoNTEMS) {
				BlockInfoNTEMS bloxs = ((BlockInfoNTEMS)block);
				blox.add(new LoadingBlock(bloxs.id, bloxs.meta).addCoords(bloxs.x, bloxs.y, bloxs.z));
			}else if(block instanceof BlockInfoTES) {
				BlockInfoTES bloxs = ((BlockInfoTES)block);
				blox.add(new LoadingBlock(bloxs.nbt.getShort("bID"), bloxs.nbt.getByte("metad"), true, bloxs.nbt).addCoords(bloxs.x, bloxs.y, bloxs.z));
			}else{
				blox.add(new LoadingBlock((short)0));
			}
		}
		return blox;
	}
	
	public static class LoadingBlock { // used for loading into world
		public short id = 0;
		public byte meta = 0;
		public boolean isTE = false;
		public NBTTagCompound TEData = null;
		public int x=0;
		public int y=0;
		public int z=0;
		public boolean hasSetCoords = false;
		
		public LoadingBlock(short id, byte meta, boolean isTE, NBTTagCompound TEData) {
			this.id=id;
			this.meta=meta;
			this.isTE=isTE;
			this.TEData = TEData;
		}
		
		public LoadingBlock(short id, byte meta) {
			this.id=id;
			this.meta=meta;
		}
		
		public LoadingBlock(short id) {
			this.id=id;
		}
		
		public LoadingBlock addCoords(int x, int y, int z) {
			this.x=x;
			this.y=y;
			this.z=z;
			hasSetCoords = true;
			return this;
		}
		
		public boolean placeBlock(World world, int x, int y, int z) { // loads a block & respectful tile entity
			boolean place = true;
			Block block = Block.blocksList[id];
			if(block!=null&&!block.canPlaceBlockAt(world, x, y, z))return false;
			place = world.setBlockAndMetadataWithNotify(x, y, z, id, meta);
			if(isTE) {
				if(TEData != null) {
					if(world.getBlockTileEntity(x, y, z)!=null) {
						TEData.setInteger("x", x);
						TEData.setInteger("y", y);
						TEData.setInteger("z", z);
						world.getBlockTileEntity(x, y, z).readFromNBT(TEData);
					}
				}
			}
			return place;
		}
	}
	
	public static abstract class BlockInfo implements Serializable { // base block storage class, no data saved
		
	}
	
	public static class BlockInfoNTEM extends BlockInfo implements Serializable { // basic block w/ meta
		public short id;
		public byte meta;
		public BlockInfoNTEM(short id, byte meta) {
			this.id=id;
			this.meta=meta;
		}
	}
	
	public static class BlockInfoNTEMC extends BlockInfoNTEM implements Serializable { // array of blocks w/ meta
		public int repeat = 0;
		public BlockInfoNTEMC(short id, byte meta, int repeats) {
			super(id, meta);
			repeat=repeats;
		}
	}
	
	public static class BlockInfoNTE extends BlockInfo implements Serializable { // simple block, no meta
		public short id;
		public BlockInfoNTE(short id) {
			this.id=id;
		}
	}
	
	public static class BlockInfoNTEC extends BlockInfoNTE implements Serializable { // array of blocks w/ no meta
		public int repeat = 0;
		public BlockInfoNTEC(short id, int repeats) {
			super(id);
			repeat=repeats;
		}
	}
	
	public static class BlockInfoTE extends BlockInfo implements Serializable { // used for TE storage
		NBTTagCompound nbt = new NBTTagCompound();
		public BlockInfoTE(World world, int x, int y, int z) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if(te != null) {
				te.writeToNBT(nbt);
			}
			nbt.setShort("bID", (short) world.getBlockId(x, y, z));
			nbt.setShort("metad", (byte) world.getBlockMetadata(x, y, z));
		}
	}
	
	public static class BlockInfoNTEMS extends BlockInfo implements Serializable { // basic block w/ meta S appendage is for coordinate sensitivity
		public short id;
		public byte meta;
		public int x;
		public int y;
		public int z;
		public BlockInfoNTEMS(short id, byte meta, int x, int y, int z) {
			this.id=id;
			this.meta=meta;
			this.x=x;
			this.y=y;
			this.z=z;
		}
	}
	
	public static class BlockInfoNTES extends BlockInfo implements Serializable { // simple block, no meta
		public short id;
		public int x;
		public int y;
		public int z;
		public BlockInfoNTES(short id, int x, int y, int z) {
			this.id=id;
			this.x=x;
			this.y=y;
			this.z=z;
		}
	}
	
	public static class BlockInfoTES extends BlockInfo implements Serializable { // used for TE storage
		NBTTagCompound nbt = new NBTTagCompound();
		public BlockInfoTES(World world, int x, int y, int z) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if(te != null) {
				te.writeToNBT(nbt);
			}
			nbt.setShort("bID", (short) world.getBlockId(x, y, z));
			nbt.setByte("metad", (byte) world.getBlockMetadata(x, y, z));
			this.x=x;
			this.y=y;
			this.z=z;
		}
		public int x;
		public int y;
		public int z;
	}
	
	private int repeats = 0;
	private short id = 0;
	private byte meta = 0;
	private short lid = 0;
	private byte lmeta = 0;
	
	public void addBlock(World world, int x, int y, int z, boolean isSensitive) {
		lid = id;
		lmeta = meta;
		id = (short)world.getBlockId(x, y, z);
		meta = (byte)world.getBlockMetadata(x, y, z);
		BlockInfo bi = null;
		if(isSensitive) {
			if(world.getBlockTileEntity(x, y, z)!=null) {
				blocks.add(new BlockInfoTES(world, x, y, z));
			}else{
				bi = meta==0?new BlockInfoNTES(lid, x, y, z):new BlockInfoNTEMS(lid, meta, x, y, z);
			}
		}else{
			if(world.getBlockTileEntity(x, y, z)!=null) {
				blocks.add(new BlockInfoTE(world, x, y, z));
			}else if(lid==id&&lmeta==meta) {
				repeats++;
				return;
			}else if(repeats>1) {
				bi = meta==0?new BlockInfoNTEC(lid, repeats):new BlockInfoNTEMC(lid, meta, repeats);
				repeats = 0;
			}else{
				repeats = 0;
				bi = meta==0?new BlockInfoNTE(lid):new BlockInfoNTEM(lid, meta);
			}
		}
		if(bi != null)blocks.add(bi);
	}
}
