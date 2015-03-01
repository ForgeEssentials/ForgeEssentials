package com.forgeessentials.core.preloader;

import com.forgeessentials.core.preloader.classloading.FEClassLoader;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class FELaunchHandler implements ITweaker
{
    public static File mcLocation, jarLocation;
    public static boolean runtimeDeobfEnabled;

    public FELaunchHandler()
    {
        MixinBootstrap.init();
        MixinEnvironment.getCurrentEnvironment().addConfiguration("mixins.forgeessentials.json");
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        this.mcLocation = gameDir;
        this.jarLocation = findJarFile();
        this.runtimeDeobfEnabled = (!(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader)
    {
        new FEClassLoader().extractLibs(mcLocation, launchClassLoader);

        // register transformers
        launchClassLoader.registerTransformer(MixinBootstrap.TRANSFORMER_CLASS);
        launchClassLoader.registerTransformer("com.forgeessentials.core.preloader.asm.FEAccessTransformer");

        // inject ourselves as a mod
        try {
            System.out.println("Force-reinjecting ForgeEssentials mod container");
            Class<?> coreModManager = Class.forName("cpw.mods.fml.relauncher.CoreModManager");
            Method mdGetLoadedCoremods = coreModManager.getDeclaredMethod("getLoadedCoremods");
            mdGetLoadedCoremods.setAccessible(true);
            List<String> loadedCoremods = (List<String>)mdGetLoadedCoremods.invoke(null);
            loadedCoremods.remove(this.jarLocation.getName());

            Method mdGetReparsedCoremods = coreModManager.getDeclaredMethod("getReparseableCoremods");
            mdGetReparsedCoremods.setAccessible(true);
            List<String> reparsedCoremods = (List<String>)mdGetReparsedCoremods.invoke(null);
            reparsedCoremods.add(this.jarLocation.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[]{};
    }

    private File findJarFile() {
        URI uri = null;
        try {
            uri = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return uri != null ? new File(uri) : null;
    }
}
