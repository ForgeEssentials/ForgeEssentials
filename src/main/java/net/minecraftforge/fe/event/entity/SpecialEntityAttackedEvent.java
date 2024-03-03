package net.minecraftforge.fe.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class SpecialEntityAttackedEvent extends EntityEvent
{
    public final DamageSource source;

    public float damage;


    public SpecialEntityAttackedEvent(Entity entity, DamageSource damageSource, float damage)
    {
        super(entity);
        this.source = damageSource;
        this.damage = damage;
    }
}