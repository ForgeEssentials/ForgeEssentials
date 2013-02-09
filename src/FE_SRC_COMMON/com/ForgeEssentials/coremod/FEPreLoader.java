package com.ForgeEssentials.coremod;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

//In the event we need to mess with ASM and such, this is the place.
//Kindly do not reference any FE classes outside the coremod package in this class.

public class FEPreLoader implements IFMLLoadingPlugin, IFMLCallHook
{

	public static File	location;

	@Override
	public String[] getLibraryRequestClass()
	{
		return new String[]
		{ "com.ForgeEssentials.coremod.Downloader" };
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return Data.transformers;
	}

	@Override
	public String getModContainerClass()
	{
		return "com.ForgeEssentials.coremod.FEModContainer";
	}

	@Override
	public String getSetupClass()
	{
		return "com.ForgeEssentials.coremod.FEPreLoader";
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		if (data.containsKey("coremodLocation"))
			location = (File) data.get("coremodLocation");
	}

	@Override
	public Void call() throws Exception
	{
		return null;
	}

}
