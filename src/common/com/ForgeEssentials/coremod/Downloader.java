package com.ForgeEssentials.coremod;

import cpw.mods.fml.relauncher.ILibrarySet;

//Kindly do not reference any FE classes outside the coremod package in this class.

public class Downloader implements ILibrarySet{

	private static String[] libraries = { "WorldEdit.jar, WorldGuard.jar" };
	private static String[] checksums = { "2190c96afbf717a01d0cdceaa772866cd1794c45", "c5f93238788238da399ec0ed1eed3e0c3af54912"};

	@Override
	public String[] getLibraries() {
		return libraries;
	}

	@Override
	public String[] getHashes() {
		return checksums;
	}

	@Override
	public String getRootURL() {
		return "https://github.com/downloads/ForgeEssentials/ForgeEssentialsMain/%s";
	}

}