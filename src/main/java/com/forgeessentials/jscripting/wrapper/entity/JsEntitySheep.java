package com.forgeessentials.jscripting.wrapper.entity;

import net.minecraft.entity.passive.EntitySheep;

public class JsEntitySheep<T extends EntitySheep> extends JsEntity<T>
{

    public JsEntitySheep(T that)
    {
        super(that);
    }

    public int getFleeceColor()
    {
        return that.getFleeceColor();
    }

    public void setFleeceColor(int color)
    {
        that.setFleeceColor(color);
    }

    public boolean isSheared()
    {
        return that.getSheared();
    }

    public void setSheared(boolean sheared)
    {
        that.setSheared(sheared);
    }

}
