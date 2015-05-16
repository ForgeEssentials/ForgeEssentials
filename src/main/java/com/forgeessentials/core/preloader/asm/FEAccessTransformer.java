package com.forgeessentials.core.preloader.asm;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class FEAccessTransformer extends AccessTransformer {

    public FEAccessTransformer() throws IOException
    {
        super("META-INF/forgeessentials_at.cfg");
        // TODO Auto-generated constructor stub
    }

}
