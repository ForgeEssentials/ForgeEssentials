package net.minecraftforge.fe.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityAttackedEvent extends EntityEvent
{
    private final DamageSource source;

    private float damage;

    public boolean result;

    public EntityAttackedEvent(Entity entity, DamageSource damageSource, float damage)
    {
        super(entity);
        this.source = damageSource;
        this.damage = damage;
        this.result = true;
    }
    
    public DamageSource getSource() { return source; }

    public float getDamage() { return damage; }

    public void setDamage(float amount) { this.damage = amount; }
}