package com.ForgeEssentials.coremod;

import cpw.mods.fml.relauncher.ILibrarySet;

//Kindly do not reference any FE classes outside the coremod package in this class.

public class Downloader implements ILibrarySet{

	private static String[] libraries = { "WorldEdit.jar", "WorldGuard.jar", "mysql-connector-java-bin.jar" };
	private static String[] checksums = { "2190c96afbf717a01d0cdceaa772866cd1794c45", "c5f93238788238da399ec0ed1eed3e0c3af54912", "3ae0cff91d7f40d5b4c7cefbbd1eab34025bdc15"};

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
		return "https://github.com/ForgeEssentials/ForgeEssentialsMain/raw/master/lib/%s";
	}

}