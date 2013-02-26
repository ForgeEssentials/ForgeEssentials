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
		if(true) {
			String[] strs = str.split(":");
			if(strs.length==2) {
				boolean isint1 = FunctionHelper.isInt(strs[0].replace("-", ""));
				boolean isint2 = FunctionHelper.isInt(strs[1].replace("-", ""));
				String id = strs[0];
				String meta = strs[1];
				int beginID = isint1?(id.contains("-")?Integer.parseInt(id.substring(0, id.indexOf("-"))):Integer.parseInt(id)):-1;
				int beginMeta = meta.contains("-")?Integer.parseInt(meta.substring(0, meta.indexOf("-"))):Integer.parseInt(meta);
				int endID = isint1?(id.contains("-")?Integer.parseInt(id.substring(id.indexOf("-")+1)):beginID+1):-1;
				int endMeta = meta.contains("-")?Integer.parseInt(meta.substring(meta.indexOf("-")+1)):beginMeta+1;
				if(beginMeta>endMeta)endMeta=beginMeta+1;
				if(beginID>endID)endID=beginID+1;
				SingularBlockInfo named = getBlockInfoFromName(strs[0], false);
				if(isValidBlockID(strs[0]) && isint1 && isint2) {
					for(int i = beginID;i<endID;i++) {
						for(int m = beginMeta;m<endMeta;m++) {
							bi.blocks.add(new SingularBlockInfo(Block.blocksList[i], m, null));
						}
					}
				}else if(named!=null && isint2) {
					for(int m = beginMeta;m<endMeta;m++) {
						bi.blocks.add(new SingularBlockInfo(named.block, m, null));
					}
				}else{
					OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str+" The ID field must either be a valid block ID, or a name of a base block.");
					return bi;
				}
			}else if(strs.length==1) {
				if(FunctionHelper.isInt(str.replace("-", ""))) {
					boolean isint = FunctionHelper.isInt(str.replace("-", ""));
					int beginID = isint?(str.contains("-")?Integer.parseInt(str.substring(0, str.indexOf("-"))):Integer.parseInt(str)):-1;
					int endID = isint?(str.contains("-")?Integer.parseInt(str.substring(str.indexOf("-")+1)):beginID+1):-1;
					for(int i = beginID;i<endID;i++) {
						bi.blocks.add(new SingularBlockInfo(Block.blocksList[i], 0, null));
					}
				}else if(name!=null) {
					bi.blocks.add(name);
				}
			}else{
				OutputHandler.chatWarning(player, "Please input a valid block identifier. "+str);
				return bi;
			}
		}
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
				try{
					String nam = FunctionHelper.getNameFromItemStack(is).toLowerCase().replace(" ", "").replace(".","");
					if(nam.equals(revised))return new SingularBlockInfo(block, m, null);
				}catch(IndexOutOfBoundsException e){
					
				}
			}
		}
		return null;
	}
}
