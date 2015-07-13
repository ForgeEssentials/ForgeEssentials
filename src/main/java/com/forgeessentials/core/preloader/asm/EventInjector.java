package com.forgeessentials.core.preloader.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Mixin config for FE hooks. Likely to be permanent until Forge gets its act together.
 */
public class EventInjector implements IMixinConfigPlugin
{

    public static List<String> injectedPatches = new ArrayList<String>();

    public static final String enableDevMixins = "forgeessentials.developermode.mixin";

    @Override
    public void onLoad(String mixinPackage)
    {

    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.contains("Portal"))
        {
            return Boolean.parseBoolean(System.getProperty(enableDevMixins));
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {

    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
        injectedPatches.add(mixinInfo.getName());
    }
}
