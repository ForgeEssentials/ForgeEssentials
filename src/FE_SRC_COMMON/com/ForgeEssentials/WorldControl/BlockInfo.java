package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;
import java.util.Random;

import com.ForgeEssentials.util.FunctionHelper;

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
		public boolean compare(SingularBlockInfo inf) {
			return inf.block.blockID==block.blockID&&(inf.meta==-1||meta==-1||inf.meta==meta);
		}
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
	
	private static String nums = "0123456789";
	
	public static boolean isInt(String str) {
		for(int chr = 0;chr<str.length()-1;chr++) {
			boolean isGood = false;
			for(int num = 0;num<nums.length()-1;num++) {
				if(str.substring(chr, chr+1).equals(nums.substring(num, num+1))) {
					isGood = true;
				}
			}
			if(!isGood)return false;
		}
		return true;
	}
	
	public static boolean isValidBlockID(String id) {
		boolean isint = isInt(id);
		if(!isint)return false;
		int nt = Integer.parseInt(id);
		if(nt<0||nt>4095) return false;
		return true;
	}
	
	public static boolean isIDMetaCombo(String str) {
		if(str.contains(":")) {
			String[] strs = str.split(":");
			if(strs.length==2) {
				if(isValidBlockID(strs[0])) {
					return true;
				}
			}
		}else if(isValidBlockID(str)){
			return true;
		}
		return false;
	}
	
	public void parseText(EntityPlayer ep, String text) {
		blocks = new ArrayList<SingularBlockInfo>();
		for(String str : text.split(",")) {
			boolean isIDCombo = isIDMetaCombo(str);
			if(!isIDCombo) {
				SingularBlockInfo bi = getBIFromName(str);
				if(bi!=null) {
					blocks.add(bi);
				}else{
					ep.sendChatToPlayer("Invalid name: "+str+" Ignoring...");
				}
			}else{
				try{
					SingularBlockInfo bi = text.contains(":")?new SingularBlockInfo(Block.blocksList[Integer.parseInt(text.substring(0, text.indexOf(":")))], Integer.parseInt(text.substring(text.indexOf(":")+1)), null):new SingularBlockInfo(Block.blocksList[Integer.parseInt(text)], 0, null);
				}catch(Exception e) {
					ep.sendChatToPlayer("Invalid id: "+str+" Ignoring...");
				}
			}
		}
	}
	
	public static SingularBlockInfo getBIFromName(String name) {
		String revised = name.toLowerCase().replace(" ", "").replace(".","");
		if(name.equals("air"))return new SingularBlockInfo((Block)null, 0, null);
		if(name.equals("ironshovel"))return new SingularBlockInfo((Block)null, 0, null);
		for(int i = 0;i<Item.itemsList.length;i++) {
			Item item = Item.itemsList[i];
			if(item==null)continue;
			if(!(item instanceof ItemBlock))continue;
			Block block = Block.blocksList[((ItemBlock)item).getBlockID()];
			if(block==null)continue;
			if(Item.itemsList[block.blockID]==null)continue;
			for(int m = 0;m<16;m++) {
				ItemStack is = new ItemStack(block, m);
				String nam = FunctionHelper.getNameFromItemStack(is).toLowerCase().replace(" ", "").replace(".","");
				if(nam.equals(revised))return new SingularBlockInfo(block, m, null);
			}
		}
		return null;
	}
}
