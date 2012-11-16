package com.ForgeEssentials.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class FELoadingPlugin implements IFMLLoadingPlugin{

	@Override
	public String[] getLibraryRequestClass() {
		return new String[] {"com.ForgeEssentials.core.Downloader"};
	}

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return "com.ForgeEssentials.core.FEModContainer";
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

}