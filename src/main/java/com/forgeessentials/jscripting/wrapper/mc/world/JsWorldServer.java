package com.forgeessentials.jscripting.wrapper.mc.world;

import net.minecraft.world.server.ServerWorld;

public class JsWorldServer extends JsWorld<ServerWorld>
{

    public static JsWorldServer get(int dim)
    {
        return JsWorld.get(dim);
    }

    public JsWorldServer(ServerWorld that)
    {
        super(that);
    }

}
