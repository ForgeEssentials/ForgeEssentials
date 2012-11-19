package com.ForgeEssentials.coremod;

import java.util.Map;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

//In the event we need to mess with ASM and such, this is the place.

public class FECoreLoader implements IFMLLoadingPlugin{

	@Override
	public String[] getLibraryRequestClass() {
		return new String[]{"com.ForgeEssentials.coremod.Downloader"};
	}

	@Override
	public String[] getASMTransformerClass() {
		// So far, so good. We don't have to use ASM and may not have to.
		return null;
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

}
