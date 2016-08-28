package com.forgeessentials.jscripting.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class JsEntityLivingBase<T extends EntityLivingBase> extends JsEntity<T>
{

    public JsEntityLivingBase(T entity)
    {
        super(entity);
    }

    public float getHealth()
    {
        return that.getHealth();
    }

    public void setHealth(float value)
    {
        that.setHealth(value);
    }

    public float getMaxHealth()
    {
        return that.getMaxHealth();
    }

    public int getTotalArmorValue()
    {
        return that.getTotalArmorValue();
    }

    public boolean canEntityBeSeen(JsEntity<Entity> other)
    {
        return that.canEntityBeSeen(other.getThat());
    }

}
