package net.minecraftforge.fe.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class EntityAttackedEvent extends EntityEvent
{

    public final DamageSource source;

    public float damage;

    public boolean result;

    public EntityAttackedEvent(Entity entity, DamageSource damageSource, float damage)
    {
        super(entity);
        this.source = damageSource;
        this.damage = damage;
        this.result = true;
    }

}
