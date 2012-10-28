package com.ForgeEssentials.WorldControl;

import java.util.HashMap;

public class IdMap extends HashMap<String, Integer> {
	
	@Override
	public Integer get(Object key) {
		Integer gt = super.get(key);
		if(gt!=null) {
			return gt;
		}
		return new Integer(0);
	}

}
