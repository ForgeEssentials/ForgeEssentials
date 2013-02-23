package com.ForgeEssentials.util;

import java.util.ArrayList;
import java.util.Random;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BlockInfo
{
	public ArrayList<SingularBlockInfo> blocks = new ArrayList<SingularBlockInfo>();
	private Random rand = new Random();
	public static class SingularBlockInfo {
		public Block block;
		public int meta;
		public NBTTagCompound nbt;
		public SingularBlockInfo(Block block, int meta, NBTTagCompound nbt) {
			this.block=block;
			this.meta=meta;
			this.nbt=nbt;
		}
		public int getBlockID() {
			return block==null?0:block.blockID;
		}
		public boolean compare(SingularBlockInfo inf) {
			return inf.getBlockID()==getBlockID()&&(inf.meta==-1||meta==-1||inf.meta==meta);
		}
	}
	
	public void merge(BlockInfo bi) {
		blocks.addAll(bi.blocks);
	}
	
	public boolean compare(SingularBlockInfo inf) {
		for(SingularBlockInfo in : blocks) {
			if(in.compare(inf))return true;
		}
		return false;
	}
	
	public SingularBlockInfo randomBlock() {
		return blocks.size()==0?null:blocks.get(blocks.size()==1?0:rand.nextInt(blocks.size()));
	}
	
	public void addBlock(Block block, int meta, NBTTagCompound nbt) {
		blocks.add(new SingularBlockInfo(block, meta, nbt));
	}
	
	public void addBlock(Block block, int meta) {
		addBlock(block, meta, null);
	}
	
	public void addBlock(Block block) {
		addBlock(block, 0, null);
	}
	
	public void addBlock() {
		addBlock(null, 0, null);
	}
	
	public static boolean isValidBlockID(String id) {
		boolean isint = FunctionHelper.isInt(id);
		if(!isint)return false;
		int nt = Integer.parseInt(id);
		if(nt<0||nt>4095) return false;
		return true;
	}
	
	public static BlockInfo parse(String str, EntityPlayer player) {
		BlockInfo bi = new BlockInfo();
		SingularBlockInfo name = getBlockInfoFromName(str, true);
		if(str.contains(":")&& FunctionHelper.isInt(str.substring(str.indexOf(":")+1))) {
			String[] strs = str.split(":");
			if(strs.length==2) {
				boolean isint = FunctionHelper.isInt(strs[1]);
				SingularBlockInfo named = getBlockInfoFromName(strs[0], false);
				if(isValidBlockID(strs[0]) && isint) {
					bi.blocks.add(new SingularBlockInfo(Block.blocksList[Integer.parseInt(strs[0])], Integer.parseInt(strs[1]), null));
				}else if(named!=null && isint) {
					bi.blocks.add(new SingularBlockInfo(named.block, Integer.parseInt(strs[1]), null));
				}else{
					OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" The ID field must either be a valid block ID, or a name of a base block.");
					return bi;
				}
			}else{
				OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" You may not have more than two values in a combo.");
				return bi;
			}
		}else if(isValidBlockID(str)) {
			bi.blocks.add(new SingularBlockInfo(Block.blocksList[Integer.parseInt(str)], 0, null));
		}else if(name!=null) {
			bi.blocks.add(name);
		}else if(str.contains("-")) {
			String begin = str.substring(0, str.indexOf("-"));
			String end = str.substring(str.indexOf("-")+1);
			if(begin.contains(":")&& !begin.contains("-") && !begin.contains("_")) {
				SingularBlockInfo first = parse(begin, player).blocks.get(0);
				if(!FunctionHelper.isInt(end)) {
					OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" Metadata must be a number.");
					return bi;
				}
				int em = Integer.parseInt(end);
				if(first.meta>=em||first.meta>15||em>15||first.meta<-1||em<-1) {
					OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" Metadata cannot be larger than 15, smaller than -1, or be smaller in the second parameter.");
					return bi;
				}
				for(int meta = first.meta;meta<em;meta++) {
					bi.blocks.add(new SingularBlockInfo(first.block, meta, null));
				}
			}else{
				OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" You cannot have ranges inside ranges, and you must specify a beginning metadata (Example: 35:0-15).");
				return bi;
			}
		}else if(str.contains("_")) {
			String begin = str.substring(0, str.indexOf("_"));
			String end = str.substring(str.indexOf("_")+1);
			if(FunctionHelper.isInt(begin) && FunctionHelper.isInt(end)) {
				int first = Integer.parseInt(begin);
				int last = Integer.parseInt(end);
				for(int id = first;id<last;id++) {
					bi.blocks.add(new SingularBlockInfo(Block.blocksList[id], 0, null));
				}
			}else{
				OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" Both IDs must be numbers.");
				return bi;
			}
		}else{
			OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+"");
			return bi;
		}
		//OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+"");
		return bi;
	}
	
	public static BlockInfo parseAll(String str, EntityPlayer player) {
		BlockInfo info = new BlockInfo();
		for(String inf : str.split(",")) {
			info.merge(parse(inf, player));
		}
		return info;
	}
	
	public static SingularBlockInfo getBlockInfoFromName(String name, boolean hasMeta) {
		String revised = name.toLowerCase().replace(" ", "").replace(".","");
		if(name.equals("air"))return new SingularBlockInfo((Block)null, 0, null);
		for(int i = 0;i<Item.itemsList.length;i++) {
			Item item = Item.itemsList[i];
			if(item==null)continue;
			if(!(item instanceof ItemBlock))continue;
			Block block = Block.blocksList[((ItemBlock)item).getBlockID()];
			if(block==null)continue;
			if(Item.itemsList[block.blockID]==null)continue;
			for(int m = 0;m<(hasMeta?16:1);m++) {
				ItemStack is = new ItemStack(block, 0, m);
				String nam = FunctionHelper.getNameFromItemStack(is).toLowerCase().replace(" ", "").replace(".","");
				if(nam.equals(revised))return new SingularBlockInfo(block, m, null);
			}
		}
		return null;
	}
}
