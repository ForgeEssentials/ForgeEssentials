package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.PlayerUtil;

public class PotionEffect extends ZoneEffect
{

    protected String potionEffects;

    public PotionEffect(EntityPlayerMP player, int interval, String potionEffects)
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
