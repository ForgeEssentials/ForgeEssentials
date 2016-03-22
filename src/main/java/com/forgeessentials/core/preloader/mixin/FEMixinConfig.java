package com.forgeessentials.core.preloader.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.common.ForgeVersion;

import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Mixin config for FE hooks. Likely to be permanent until Forge gets its act together.
 */
public class FEMixinConfig implements IMixinConfigPlugin
{

    protected static List<String> injectedPatches = new ArrayList<String>();

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
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {
        /* do nothing */
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.contains("fml.common.eventhandler.MixinEventBus"))
        {
            return (!(ForgeVersion.buildVersion < 1517));
        }
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
