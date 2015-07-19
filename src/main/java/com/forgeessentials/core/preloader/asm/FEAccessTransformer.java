package com.forgeessentials.core.preloader.asm;

import java.io.IOException;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class FEAccessTransformer extends AccessTransformer
{

    public FEAccessTransformer() throws IOException
    {
        super("META-INF/forgeessentials_at.cfg");
        // TODO Auto-generated constructor stub
    }

}
