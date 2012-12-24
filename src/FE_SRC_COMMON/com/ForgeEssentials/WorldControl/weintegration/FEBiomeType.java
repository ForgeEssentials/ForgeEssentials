package com.ForgeEssentials.WorldControl.weintegration;

import java.lang.reflect.Field;
import java.util.Locale;
import com.sk89q.worldedit.BiomeType;

//May not be compatible with ExtraBiomesXL or Biomes O'Plenty.

public enum FEBiomeType implements BiomeType {
	
	public static final BiomeGenBase[], BiomeGenBase.biomeList;
	
	OCEAN(Biome.biomeList[0]),
	PLAINS(Biome.biomeList[1]),
	DESERT(Biome.biomeList[2]),
	EXTREMEHILLS(Biome.biomeList[3]),
	FOREST(Biome.biomeList[4]),
	TAIGA(Biome.biomeList[5]),
	SWAMPLAND(Biome.biomeList[6]),
	RIVER(Biome.biomeList[7]),
	HELL(Biome.biomeList[8]),
	SKY(Biome.biomeList[9]),
	FROZENOCEAN(Biome.biomeList[10]),
	FROZENRIVER(Biome.biomeList[11]),
	ICEPLAINS(Biome.biomeList[12]),
	ICEMOUNTAINS(Biome.biomeList[13]),
	MUSHROOMISLAND(Biome.biomeList[14]),
	MUSHROOMISLANDSHORE(Biome.biomeList[15]),
	BEACH(Biome.biomeList[16]),
	DESERTHILLS(Biome.biomeList[17]),
	FORESTHILLS(Biome.biomeList[18]),
	TAIGAHILLS(Biome.biomeList[19]),
	EXTREMEHILLSEDGE(Biome.biomeList[20]),
	JUNGLE(Biome.biomeList[21]),
	JUNGLEHILLS(Biome.biomeList[22]);

	private ConsoleBiomeType(Biome biome) {
		this.biome = biome;
	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ENGLISH);
	}

	public Biome getSPCBiome() {
		return biome;
	}
	
	public int getBiomeID() {
		return biome.getBiomeID();
	}

	public String getBiomeName() {
		return biome.getBiomeName();
	}

}
