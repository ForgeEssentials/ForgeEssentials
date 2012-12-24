package com.ForgeEssentials.WorldControl.weintegration;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeWrapper {

	private BiomeGenBase biome; // Biome
	public static final BiomeGenBase[] biomeList = BiomeGenBase.biomeList;

	public Biome(BiomeGenBase biome) {
		this.biome = biome;
	}

	public int getBiomeID() {
		return biome.worldGeneratorTrees;
	}

	public String getBiomeName() {
		return biome.field_76772_y;
	}

	public yy getBiomeGenBase() {
		return biome;
	}

	static {
		int index = 0;
		for(yy biome : obfBiomeList) {
			biomeList[index] = new Biome(biome);
			index++;
		}
	}

}