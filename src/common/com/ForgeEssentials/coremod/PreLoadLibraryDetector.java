package com.ForgeEssentials.coremod;

import java.io.File;

//Called by BOTH preloader and main mod to verify presence of libraries

public class PreLoadLibraryDetector {
	
	public static boolean present;
	private static boolean wepresent;
	private static boolean wgpresent;
	
	static File we = new File ("./lib/WorldEdit.jar");
	static File wg = new File ("./lib/WorldGuard.jar");
	
	public static void checkPresence(){
		if (we.exists()){
			wepresent = true;
		}
		if (wg.exists()){
			wgpresent = true;
		}
		if (wepresent = true){
			if (wgpresent = true){
				present = true;
			}
			else present = false;
		}
	}
	public static boolean report(){
		return present;
	}

}
