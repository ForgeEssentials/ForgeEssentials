package com.forgeessentials.jscripting.wrapper.mc.entity;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;

public class JsEntitySheep<T extends EntitySheep> extends JsEntity<T>
{

    public JsEntitySheep(T that)
    {
        super(that);
    }

    public int getFleeceColor()
    {
        return that.getFleeceColor().ordinal();
    }

    public void setFleeceColor(int color)
    {
        that.setFleeceColor(EnumDyeColor.byMetadata(color));
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
