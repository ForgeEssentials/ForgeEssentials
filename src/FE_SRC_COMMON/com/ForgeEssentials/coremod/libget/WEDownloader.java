package com.ForgeEssentials.coremod.libget;

import cpw.mods.fml.relauncher.ILibrarySet;

public class WEDownloader implements ILibrarySet{

	private static String[] we = {"worldedit-5.4.6-SNAPSHOT.jar"};
	private static String[] wehash = {"de96549f9c31aa9268f649fc6757e7b68b180549"};
	
	@Override
	public String[] getLibraries() {
		return we;
	}

	@Override
	public String[] getHashes() {
		return wehash;
	}

	@Override
	public String getRootURL() {
		return "https://github.com/ForgeEssentials/ForgeEssentialsMain/raw/master/lib/%s";
		//return "http://build.sk89q.com/job/WorldEdit/1309/com.sk89q$worldedit/artifact/com.sk89q/worldedit/5.4.6-SNAPSHOT/%s";
	}

}
