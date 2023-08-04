package com.forgeessentials.jscripting.wrapper.mc.entity;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;

public class JsEntitySheep<T extends SheepEntity> extends JsEntity<T>
{

    public JsEntitySheep(T that)
    {
        super(that);
    }

    public int getFleeceColor()
    {
        return that.getColor().ordinal();
    }

    public void setFleeceColor(int color)
    {
        that.setColor(DyeColor.byId(color));
    }

    public boolean isSheared()
    {
        return that.isSheared();
    }

    public void setSheared(boolean sheared)
    {
        that.setSheared(sheared);
    }

}
