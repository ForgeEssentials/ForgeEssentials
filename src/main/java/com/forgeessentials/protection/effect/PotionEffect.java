package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.FunctionHelper;

public class PotionEffect extends ZoneEffect {

    protected String potionEffects;

    public PotionEffect(EntityPlayerMP player, int interval, String potionEffects)
    {
        super(player, interval, false);
        this.potionEffects = potionEffects;
    }

    @Override
    public void execute()
    {
        FunctionHelper.applyPotionEffects(player, potionEffects);
    }

}
