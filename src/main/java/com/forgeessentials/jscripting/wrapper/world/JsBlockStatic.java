package com.forgeessentials.jscripting.wrapper.world;

public class JsBlockStatic
{

    /**
     * @deprecated Use mc.world.Block.get(name) instead
     */
    @Deprecated
    public JsBlock getBlock(String name)
    {
        return JsBlock.get(name);
    }

}
