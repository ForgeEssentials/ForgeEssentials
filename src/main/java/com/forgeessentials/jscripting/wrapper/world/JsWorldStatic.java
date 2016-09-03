package com.forgeessentials.jscripting.wrapper.world;

public class JsWorldStatic
{

    /**
     * @deprecated Use mc.world.World.get(dim) instead
     */
    @Deprecated
    public JsWorldServer getWorld(int dim)
    {
        return JsWorldServer.get(dim);
    }

}
