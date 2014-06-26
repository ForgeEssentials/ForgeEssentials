package com.forgeessentials.core.preloader;

import com.forgeessentials.core.preloader.classloading.FEClassLoader;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.Map;

//In the event we need to mess with ASM and such, this is the place.
//Kindly do not reference any FE classes outside the coremod package in this class.

public class FEPreLoader implements IFMLLoadingPlugin, IFMLCallHook {

    public static File location;
    private LaunchClassLoader classLoader;
    private File FEfolder;

    @Override
    public String[] getASMTransformerClass()
    {
        return Data.transformers;
    }

    @Override
    public String getModContainerClass()
    {
        return "com.forgeessentials.core.preloader.FEModContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "com.forgeessentials.core.preloader.FEPreLoader";
    }

    @Override
    public String getAccessTransformerClass()
    {
        return "com.forgeessentials.core.preloader.asm.FEAccessTransformer";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

        if (data.containsKey("mcLocation"))
        {
            FEfolder = new File((File) data.get("mcLocation"), "ForgeEssentials");
        }
        if (data.containsKey("classLoader") && data.get("classLoader") != null)
        {
            classLoader = (LaunchClassLoader) data.get("classLoader");
        }
    }

    // leave this here, somehow mc crashes without it.
    @Override
    public Void call() throws Exception
    {
        new FEClassLoader().runClassLoad(classLoader, FEfolder);
        return null;
    }

}
