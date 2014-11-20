package com.forgeessentials.core.preloader;

import com.forgeessentials.core.preloader.classloading.FEClassLoader;
import com.forgeessentials.core.preloader.forge.FEHooks;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.Map;

//In the event we need to mess with ASM and such, this is the place.
//Kindly do not reference any FE classes outside the coremod package in this class.

@MCVersion("1.7.10")
@SortingIndex(1001)
public class FEPreLoader implements IFMLLoadingPlugin, IFMLCallHook
{

    public static boolean runtimeDeobfEnabled;

    public static File mcLocation;
    public static LaunchClassLoader classLoader;

    private String[] transformers = { "com.forgeessentials.core.preloader.asm.EventInjector" };

    @Override
    public String[] getASMTransformerClass()
    {
        FEHooks.doInit();
        return transformers;
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

        if (data.containsKey("mcLocation") && data.get("mcLocation") != null)
        {
            mcLocation = (File) data.get("mcLocation");
        }
        if (data.containsKey("classLoader") && data.get("classLoader") != null)
        {
            classLoader = (LaunchClassLoader) data.get("classLoader");
        }
        if (data.containsKey("runtimeDeobfuscationEnabled") && data.get("runtimeDeobfuscationEnabled") != null)
        {
            runtimeDeobfEnabled = (boolean) data.get("runtimeDeobfuscationEnabled");
        }
    }

    @Override
    public Void call() throws Exception
    {
        new FEClassLoader().extractLibs(mcLocation);
        return null;
    }
}
