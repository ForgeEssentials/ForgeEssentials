package com.forgeessentials.core.preloader;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class FELoadingPlugin implements IFMLLoadingPlugin
{
    @Override public String[] getASMTransformerClass()
    {
        return new String[] { EventTransformer.class.getName() };
    }

    @Override public String getModContainerClass()
    {
        return null;
    }

    @Override public String getSetupClass()
    {
        return null;
    }

    @Override public void injectData(Map<String, Object> map)
    {

    }

    @Override public String getAccessTransformerClass()
    {
        return null;
    }
}
