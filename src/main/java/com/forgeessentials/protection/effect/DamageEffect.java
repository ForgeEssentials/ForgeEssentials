package com.forgeessentials.protection.effect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class DamageEffect extends ZoneEffect
{

    protected int damage;

    public DamageEffect(ServerPlayer player, int interval, int damage)
    {
        super(player, interval, true);
        this.damage = damage;
    }

    @Override
    public void execute()
    {
        player.hurt(DamageSource.GENERIC, damage);
    }

}
