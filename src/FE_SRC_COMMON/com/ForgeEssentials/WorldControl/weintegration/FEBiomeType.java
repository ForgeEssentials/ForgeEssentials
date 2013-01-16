package com.ForgeEssentials.WorldControl.weintegration;

import net.minecraft.world.biome.BiomeGenBase;

import com.sk89q.worldedit.BiomeType;

//May not be compatible with ExtraBiomesXL or Biomes O'Plenty.

public class FEBiomeType implements BiomeType {
	
	protected BiomeGenBase biome;
	public FEBiomeType(BiomeGenBase biome) {
		this.biome = biome;
	}

	@Override
	public String getName() {
		return biome.biomeName;
	}
}


