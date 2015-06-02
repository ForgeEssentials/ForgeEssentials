package com.forgeessentials.worldborder.effect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectPotion extends WorldBorderEffect implements Loadable
{

    public static class PotionEffectData
    {

        public int id;

        public int duration;

        public int modifier;

        public PotionEffectData(int id, int duration, int modifier)
        {
            this.id = id;
            this.duration = duration;
            this.modifier = modifier;
        }

    }

    public List<PotionEffectData> potionEffects;

    public int interval = 2000;

    public EffectPotion()
    {
        potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffectData(9, 5, 0));
    }

    @Override
    public void afterLoad()
    {
        if (potionEffects == null)
        {
            potionEffects = new ArrayList<>();
            potionEffects.add(new PotionEffectData(9, 5, 0));
        }
    }

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            doEffect(player);
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            doEffect(player);
            pi.startTimeout(this.getClass().getName(), interval * 1000);
        }
    }

    public void doEffect(EntityPlayerMP player)
    {
        for (PotionEffectData effect : potionEffects)
            player.addPotionEffect(new PotionEffect(effect.id, effect.duration, effect.modifier));
    }

}
