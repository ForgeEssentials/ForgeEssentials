package com.forgeessentials.jscripting.wrapper.mc.entity;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class JsSheepEntity<T extends Sheep> extends JsEntity<T>
{

    public JsSheepEntity(T that)
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
