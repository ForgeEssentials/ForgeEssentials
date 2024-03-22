package com.forgeessentials.protection.effect;

import com.forgeessentials.util.PlayerUtil;

import net.minecraft.server.level.ServerPlayer;

public class PotionEffect extends ZoneEffect
{

    protected String potionEffects;

    public PotionEffect(ServerPlayer player, int interval, String potionEffects)
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
