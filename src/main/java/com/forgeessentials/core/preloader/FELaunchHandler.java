package com.forgeessentials.core.preloader;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;

public class FELaunchHandler
{

    /* ------------------------------------------------------------ */
    //TODO replace or remove
    public void plsRemove()
    {
        // Fix CoFH compatibility. Fixes #1903
        MixinEnvironment.getEnvironment(Phase.PREINIT).addTransformerExclusion("cofh.asm.CoFHAccessTransformer");
        // Enable FastCraft compatibility mode
        System.setProperty("fastcraft.asm.permissive", "true");
    }
}
