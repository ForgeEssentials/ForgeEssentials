package com.forgeessentials.multiworld.v2.genWorld;

import java.io.File;

import com.mojang.datafixers.DataFixer;

import net.minecraft.world.level.storage.DimensionDataStorage;
//not used rn
public class MiltiworldDimensionSavedDataManager extends DimensionDataStorage {

	public MiltiworldDimensionSavedDataManager(File dataFolder, DataFixer dataFixer) {
		super(dataFolder,  dataFixer);
	}

}
