package com.forgeessentials.multiworld.v2;

import java.io.File;

import com.mojang.datafixers.DataFixer;

import net.minecraft.world.storage.DimensionSavedDataManager;

public class MiltiworldDimensionSavedDataManager extends DimensionSavedDataManager {

	public MiltiworldDimensionSavedDataManager(File dataFolder, DataFixer dataFixer) {
		super(dataFolder,  dataFixer);
	}

}
