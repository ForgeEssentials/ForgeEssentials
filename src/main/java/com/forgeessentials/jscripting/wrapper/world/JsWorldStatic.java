package com.forgeessentials.jscripting.wrapper.world;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class JsWorldStatic
{

    public JsWorldServer getWorld(int dim)
    {
        WorldServer world = DimensionManager.getWorld(dim);
        return world == null ? null : new JsWorldServer(world);
    }

}
