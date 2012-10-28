package com.ForgeEssentials.commands;

import java.util.ArrayList;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;

public class CommandProcesser {
	
	public static CommandInfo processIDMetaCombo(String str) {
		CommandInfo info = new CommandInfo();
		String[] ids = str.split(",");
		for(int i = 0;i<ids.length;i++) {
			String ids2 = ids[i];
			if(!ids2.contains(":")&&ids2.length()>0) {
				IDMETA inf = getIDFromName(ids2);
				if(inf.id>=0)info.setInfo(inf.id, inf.meta);
			}else if(ids2.length()>2){
				IDMETA inf= getIDFromName(ids2.substring(0, ids2.indexOf(":")));
				int dmeta = Integer.parseInt(ids2.substring(ids2.indexOf(":")+1));
				if(dmeta==-1) {
					for(int m = 0;m<128;m++) {
						if(inf.id>=0)info.setInfo(inf.id, m);
					}
				}else{
					if(inf.id>=0)info.setInfo(inf.id, inf.meta>0?inf.meta:dmeta);
				}
			}
		}
		return info;
	}
	
	public static class IDMETA {
		int id =-1;
		int meta = 0;
		public IDMETA(int id, int meta) {
			this.id=id;
			this.meta=meta;
		}
	}
	
	public static IDMETA getIDFromName(String name) {
		String revised = name.toLowerCase().replace(" ", "").replace(".","");
		try{
			return new IDMETA(Integer.parseInt(name), 0);
		}catch(Exception e) {
			
		}
		if(name.equals("air"))return new IDMETA(0, 0);
		if(name.equals("ironshovel"))return new IDMETA(0, 0);
		for(int i = 0;i<Block.blocksList.length;i++) {
			Block block = Block.blocksList[i];
			if(block==null)continue;
			if(Item.itemsList[block.blockID]==null)continue;
			ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
			block.getSubBlocks(block.blockID, null, subItems);
			if(subItems.size()>0&&subItems.get(0).getItemDamage()!=0) {
				subItems.add(new ItemStack(block, 1, 0));
			}
			for(int meta = 0;meta<subItems.size();meta++) {
				ItemStack item = new ItemStack(block, 1, subItems.get(meta).getItemDamage());
				String iName = (String)item.func_82833_r();
				iName=iName.toLowerCase().replace(" ", "").replace(".", "");
				if(iName.equals(revised)) {
					return new IDMETA(i, subItems.get(meta).getItemDamage());
				}
			}
		}
		return new IDMETA(-1, 0);
	}
}
