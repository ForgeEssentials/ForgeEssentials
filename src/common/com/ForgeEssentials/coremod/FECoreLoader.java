package com.ForgeEssentials.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

//In the event we need to mess with ASM and such, this is the place.
//Kindly do not reference any FE classes outside the coremod package in this class.

public class FECoreLoader implements IFMLLoadingPlugin, IFMLCallHook{

	@Override
	public String[] getLibraryRequestClass() {
		return new String[]{"com.ForgeEssentials.coremod.Downloader"};
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"com.ForgeEssentials.coremod.FEPermissionsTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "com.ForgeEssentials.coremod.FEModContainer";
	}

	@Override
	public String getSetupClass() {
		return "com.ForgeEssentials.coremod.FECoreLoader";
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// We don't need this yet.
	}

	@Override
	public Void call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
