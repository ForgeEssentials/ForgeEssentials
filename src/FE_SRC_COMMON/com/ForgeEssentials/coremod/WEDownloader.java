package com.ForgeEssentials.coremod;

import cpw.mods.fml.relauncher.ILibrarySet;

public class WEDownloader implements ILibrarySet{

	private static String[] we = {"worldedit-5.4.6-SNAPSHOT.jar"};
	private static String[] wehash = {"897c73e7864b4a130933c64f138b8cf5547792ac"};
	
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
		return "http://build.sk89q.com/job/WorldEdit/1309/com.sk89q$worldedit/artifact/com.sk89q/worldedit/5.4.6-SNAPSHOT/%s";
	}

}
