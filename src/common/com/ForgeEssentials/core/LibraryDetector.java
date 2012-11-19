package com.ForgeEssentials.core;

import cpw.mods.fml.common.Loader;

public class LibraryDetector {
	
	public static boolean libFound;
	public static boolean preLoaderFound;
	
	public void detect(){
		OutputHandler.SOP("Testing to see if preloader, WorldEdit and WorldControl are downloaded successfully.");
		// look for preloader
		preLoaderFound = Loader.isModLoaded("FEPreLoader");
		if (preLoaderFound = true){
			OutputHandler.SOP("Preloader successfully loaded.");
			try{
				// For now, this crashes minecraft if coremod is not found. If anyone knows a way to gracefully intercept, please commit.
				libFound = com.ForgeEssentials.coremod.PreLoadLibraryDetector.report();
				if (libFound = true){
					OutputHandler.SOP("WorldGuard and WorldEdit found in lib folder.");
				}
			}
			finally{
				
			}
		}
	}
}
	