package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;

public class DamageEffect extends ZoneEffect
{

    protected int damage;

    public DamageEffect(ServerPlayerEntity player, int interval, int damage)
    {
        super(player, interval, true);
        this.damage = damage;
    }

    @Override
    public void execute()
    {
        player.attackEntityFrom(DamageSource.GENERIC, damage);
    }

}
