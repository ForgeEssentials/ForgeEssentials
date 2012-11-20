package com.ForgeEssentials.core;

import java.io.File;

import cpw.mods.fml.common.Loader;

public class LibraryDetector {
	
	
	public static boolean preLoaderFound;
	public static boolean wepresent;
	
	static File we = new File ("./lib/WorldEdit.jar");
	static File wg = new File ("./lib/WorldGuard.jar");
	
	public void detect(){
		OutputHandler.SOP("Testing to see if preloader, WorldEdit and WorldControl are downloaded successfully.");
		// look for preloader
		preLoaderFound = Loader.isModLoaded("FEPreLoader");
		if (preLoaderFound = true){
			OutputHandler.SOP("Preloader successfully loaded.");
			if (we.exists()){
				OutputHandler.SOP("WorldEdit jar found. Enabling WorldEdit compatibility.");
				wepresent = true;
			}
			if (wg.exists()){
				OutputHandler.SOP("WorldGuard jar found. Enabling WorldGuard comaptiblility.");
			}
			
			
		}
	}
}
	