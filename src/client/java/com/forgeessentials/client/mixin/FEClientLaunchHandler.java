package com.forgeessentials.client.mixin;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import org.spongepowered.asm.launch.MixinTweaker;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class FEClientLaunchHandler implements ITweaker
{

    private MixinTweaker mixinTweaker;

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

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        // Initialize Mixin
        mixinTweaker = new MixinTweaker();
        mixinTweaker.acceptOptions(args, gameDir, assetsDir, profile);
        MixinEnvironment.getDefaultEnvironment().addConfiguration("mixins.forgeessentials.client.json");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        mixinTweaker.injectIntoClassLoader(classLoader);
    }

}
