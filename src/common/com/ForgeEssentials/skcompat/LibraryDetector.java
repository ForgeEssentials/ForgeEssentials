package com.ForgeEssentials.skcompat;

import java.io.File;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;

public class LibraryDetector {
	
	//needs localization.
	public static boolean preLoaderFound;
	public static boolean wepresent;
	
	static File we = new File ("./lib/WorldEdit.jar");
	static File wg = new File ("./lib/WorldGuard.jar");
	
	public static void detect(){
		OutputHandler.SOP("Testing to see if preloader, WorldEdit and WorldControl are downloaded successfully.");
		// look for preloader
		preLoaderFound = Loader.isModLoaded("FEPreLoader");
		if (preLoaderFound){
			OutputHandler.SOP("Preloader successfully loaded.");
		}
		else{
			OutputHandler.SOP("Couldn't find preloader. Either you are using core only package or you have a corrupt install.");
		}
		if (we.exists()){
			OutputHandler.SOP("WorldEdit jar found. Enabling WorldEdit compatibility.");
			wepresent = true;
		}
		if (wg.exists()){
			OutputHandler.SOP("WorldGuard jar found. Enabling WorldGuard comaptiblility.");
		}
		
	}
}
	