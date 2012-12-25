package com.ForgeEssentials.WorldControl.weintegration;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;

import com.sk89q.worldedit.BiomeTypes;
import com.sk89q.worldedit.ServerInterface;


public class FEServerInterface extends ServerInterface {
	private final com.ForgeEssentials.WorldControl.weintegration.FEBiomeTypes biomeTypes;

	public FEServerInterface() {
		this.biomeTypes = new com.ForgeEssentials.WorldControl.weintegration.FEBiomeTypes();
	}

	@Override
	public BiomeTypes getBiomes() {
		return biomeTypes;
	}

	@Override
	public boolean isValidMobType(String arg0) {
		return EntityList.stringToClassMapping.containsKey(arg0) && EntityLiving.class.isAssignableFrom((Class)EntityList.stringToClassMapping.get(arg0));
	}

	@Override
	public void reload() {

	}

	@Override
	public int resolveItem(String arg0) {
		return 0;
	}
}