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
		public BlockInfoTE(World world, int x, int y, int z, int rX, int rY, int rZ) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if(te != null) {
				te.writeToNBT(nbt);
			}
			nbt.setInteger("rX", rX);
			nbt.setInteger("rY", rY);
			nbt.setInteger("rZ", rZ);
		}
	}
	
	private int repeats = 0;
	private short id = 0;
	private byte meta = 0;
	private short lid = 0;
	private byte lmeta = 0;
	
	public void addBlock(World world, int x, int y, int z) {
		lid = id;
		lmeta = meta;
		id = (short)world.getBlockId(x, y, z);
		meta = (byte)world.getBlockId(x, y, z);
		BlockInfo bi = null;
		if(lid==id&&lmeta==meta) {
			repeats++;
			return;
		}else if(repeats>1) {
			bi = meta==0?new BlockInfoNTEC(lid, repeats):new BlockInfoNTEMC(lid, meta, repeats);
			repeats = 0;
		}else{
			repeats = 0;
			bi = meta==0?new BlockInfoNTE(lid):new BlockInfoNTEM(lid, meta);
		}
		if(bi != null)blocks.add(bi);
	}
	
	public void load(World world, int x, int y, int z) {
		for(BlockInfo block : blocks) {
			
		}
	}
}
