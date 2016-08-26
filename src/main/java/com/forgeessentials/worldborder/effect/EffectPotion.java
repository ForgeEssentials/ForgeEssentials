package com.forgeessentials.worldborder.effect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> <effect> <seconds> <amplifier>
 */
public class EffectPotion extends WorldBorderEffect implements Loadable
{

    public int id;

    public int duration;

    public int modifier;

    public int interval;

    public EffectPotion()
    {
    }

    @Override
    public boolean provideArguments(String[] args)
    {
        if (args.length < 4)
            return false;
        interval = Integer.parseInt(args[0]);
        id = Integer.parseInt(args[1]);
        duration = Integer.parseInt(args[2]);
        modifier = Integer.parseInt(args[3]);

        return true;
    }

    @Override
    public void afterLoad()
    {
        if ((Integer) id == null)
        {
            id = 9;
            duration = 5;
            modifier = 0;
        }
    }

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            player.addPotionEffect(new PotionEffect(Potion.getPotionById(id), duration, modifier));
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.addPotionEffect(new PotionEffect(Potion.getPotionById(id), duration, modifier));
            pi.startTimeout(this.getClass().getName(), interval * 1000);
        }
    }

    public String getSyntax()
    {
        return "<interval> <effect> <seconds> <amplifier>";
    }

    public String toString()
    {
        return String.format("potion interval: %d1 id: %d2 duration: %d3 amplifier: %d4", interval, id, duration, modifier);
    }

}
