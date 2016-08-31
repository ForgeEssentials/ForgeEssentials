package com.forgeessentials.jscripting.wrapper;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class JsWorldStatic
{

    public JsWorld<WorldServer> getWorld(int dim)
    {
        WorldServer world = DimensionManager.getWorld(dim);
        return world == null ? null : new JsWorld<>(world);
    }

}
