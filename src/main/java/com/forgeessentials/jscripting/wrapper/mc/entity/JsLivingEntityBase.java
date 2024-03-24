package com.forgeessentials.jscripting.wrapper.mc.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;

public class JsLivingEntityBase<T extends LivingEntity> extends JsEntity<T>
{

    public JsLivingEntityBase(T that)
    {
        super(that);
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
        return that.getArmorValue();
    }

    public boolean canEntityBeSeen(JsEntity<LivingEntity> other)
    {
        return BehaviorUtils.canSee(that, other.getThat());
    }

}
