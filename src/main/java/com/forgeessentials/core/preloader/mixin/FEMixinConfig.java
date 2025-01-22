package com.forgeessentials.core.preloader.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.forgeessentials.core.preloader.FELaunchHandler;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Mixin config for FE hooks. Likely to be permanent until Forge gets its act together.
 */
public class FEMixinConfig implements IMixinConfigPlugin
{

    public static int javaVersion;

    static {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        }
        javaVersion = Integer.parseInt(version);
    }

    protected static List<String> injectedPatches = new ArrayList<>();

    @Override
    public void onLoad(String mixinPackage)
    {
        /* do nothing */
    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public List<String> getMixins()
    {
        List<String> mixins = new ArrayList<>();
        if (FELaunchHandler.isCauldron) {
            //Add the mixin that is specific for when the server is Cauldron and/or it's forks.
            mixins.add(Mixins.MixinNetHandlerPlayServerCauldron.getMixinRelativePath());
        } else {
            mixins.add(Mixins.MixinNetHandlerPlayServerForge.getMixinRelativePath());
        }

        return mixins;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {
        /* do nothing */
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return true;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
        /* do nothing */
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
        injectedPatches.add(mixinInfo.getName());
    }

    public static List<String> getInjectedPatches()
    {
        return injectedPatches;
    }

}
