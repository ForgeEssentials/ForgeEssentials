package com.forgeessentials.jscripting.wrapper;

import net.minecraft.world.World;

public class JsWorld<T extends World>
{

    protected T that;

    public JsWorld(T world)
    {
        this.that = world;
    }

    public T getThat()
    {
        return that;
    }

    public int getDimension()
    {
        return that.provider.dimensionId;
    }

}
