package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandInfo {
	public List<Integer> bid = new ArrayList<Integer>();
	public List<Integer> meta = new ArrayList<Integer>();
	public int[] getInfo(int i) {
		return new int[]{bid.get(i),meta.get(i)};
	}
	
	public int getSize() {
		return bid.size();
	}
	
	public CommandInfo() {
		
	}

	public CommandInfo(int id, int meta) {
		setInfo(id, meta);
	}
	
	public boolean isGoodInfo(int blockID, int metadata) {
		for(int i = 0;i<getSize();i++) {
			int[] temp = getInfo(i);
			if(blockID==temp[0]&&metadata==temp[1]) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isGoodInfo(int blockID) {
		for(int i = 0;i<getSize();i++) {
			int[] temp = getInfo(i);
			if(blockID==temp[0]) {
				return true;
			}
		}
		return false;
	}
	
	public void setInfo(int id, int met) {
		bid.add(id);
		meta.add(met);
	}
}
