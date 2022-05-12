package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.ServerPlayerEntity;

import com.forgeessentials.util.PlayerUtil;

public class PotionEffect extends ZoneEffect
{

    protected String potionEffects;

    public PotionEffect(ServerPlayerEntity player, int interval, String potionEffects)
    {
        super(player, interval, false);
        this.potionEffects = potionEffects;
    }

    @Override
    public void execute()
    {
        PlayerUtil.applyPotionEffects(player, potionEffects);
    }

}
