package com.ForgeEssentials.core.preloader;

import cpw.mods.fml.relauncher.ILibrarySet;

//Kindly do not reference any FE classes outside the coremod package in this class.

public class Downloader implements ILibrarySet
{

	@Override
	public String[] getLibraries()
	{
		return Data.libraries;
	}

	@Override
	public String[] getHashes()
	{
		return Data.checksums;
	}

	@Override
	public String getRootURL()
	{
		return "https://github.com/ForgeEssentials/ForgeEssentialsMain/raw/master/lib/%s";
	}

}
