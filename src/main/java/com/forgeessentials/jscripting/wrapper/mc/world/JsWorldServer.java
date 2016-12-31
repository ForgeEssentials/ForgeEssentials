package com.forgeessentials.jscripting.wrapper.mc.world;

import net.minecraft.world.WorldServer;

public class JsWorldServer extends JsWorld<WorldServer>
{

    public static JsWorldServer get(int dim)
    {
        return JsWorld.get(dim);
    }

    public JsWorldServer(WorldServer that)
    {
        super(that);
    }

}
