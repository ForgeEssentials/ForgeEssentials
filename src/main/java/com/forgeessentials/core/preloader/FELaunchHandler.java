package com.forgeessentials.core.preloader;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.forgeessentials.core.preloader.classloading.FEClassLoader;

public class FELaunchHandler implements ITweaker
{

    public static File mcLocation, jarLocation;
    public static boolean runtimeDeobfEnabled;
    private static ITweaker mixinTweaker;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        // fastcraft compat hack
        System.setProperty("fastcraft.asm.permissive", "true");

        mcLocation = gameDir;
        jarLocation = findJarFile();
        runtimeDeobfEnabled = (!(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

        // initiate mixin tweaker

        try
        {
            Class<?> mixinTweakClass = Class.forName("org.spongepowered.asm.launch.MixinTweaker");
            mixinTweaker = (ITweaker) mixinTweakClass.newInstance();
            mixinTweaker.acceptOptions(args, gameDir, assetsDir, profile);
        }
        catch (Exception e)
        {
            System.err.println("[ForgeEssentials] There was a problem initiating the Mixin subsystem.");
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader)
    {
        mixinTweaker.injectIntoClassLoader(launchClassLoader);
        new FEClassLoader().extractLibs(mcLocation, launchClassLoader);
    }

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[] {};
    }

    private File findJarFile()
    {
        URI uri = null;
        try
        {
            uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
        return uri != null ? new File(uri) : null;
    }
}
