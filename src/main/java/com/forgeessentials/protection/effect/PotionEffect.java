package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.EntityPlayerMP;

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
        String[] effects = potionEffects.split(","); // example = 9:5:0
        for (String poisonEffect : effects)
        {
            String[] effectValues = poisonEffect.split(":");
            player.addPotionEffect(
                    new net.minecraft.potion.PotionEffect(Integer.parseInt(effectValues[0]), Integer.parseInt(effectValues[1]) * 20, Integer.parseInt(effectValues[2])));
        }
    }

}
