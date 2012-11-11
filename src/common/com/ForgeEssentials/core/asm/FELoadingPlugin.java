package com.ForgeEssentials.core.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class FELoadingPlugin implements IFMLLoadingPlugin{

	@Override
	public String[] getLibraryRequestClass() {
		// do we really need libs?
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"com.ForgeEssentials.core.asm.FEAccessTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "com.ForgeEssentials.core.asm.FEModContainer";
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
