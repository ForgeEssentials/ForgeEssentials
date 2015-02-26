package com.forgeessentials.core.preloader;

import com.forgeessentials.core.preloader.classloading.FEClassLoader;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.util.Map;

//In the event we need to mess with ASM and such, this is the place.
//Kindly do not reference any FE classes outside the coremod package in this class.

@MCVersion("1.7.10")
@SortingIndex(1001)
public class FEPreLoader implements IFMLLoadingPlugin, IFMLCallHook
{

    public static boolean runtimeDeobfEnabled;

    public static File mcLocation, jarLocation;
    public static LaunchClassLoader classLoader;

    public FEPreLoader()
    {
        MixinEnvironment.getCurrentEnvironment().addConfiguration("mixins.forgeessentials.json");
    }

    private String[] transformers = { MixinBootstrap.TRANSFORMER_CLASS };

    @Override
    public String[] getASMTransformerClass()
    {
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
        if (data.containsKey("coremodLocation") && data.get("coremodLocation") != null)
        {
            jarLocation = (File) data.get("coremodLocation");
        }
    }

    @Override
    public Void call() throws Exception
    {
        new FEClassLoader().extractLibs(mcLocation);
        return null;
    }
}
