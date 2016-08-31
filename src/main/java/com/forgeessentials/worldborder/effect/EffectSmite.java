package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval>
 */
public class EffectSmite extends WorldBorderEffect
{

    public int interval;

    @Override
    public boolean provideArguments(String[] args)
    {
        if (args.length < 1)
            return false;
        interval = Integer.parseInt(args[0]);
        return true;
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
        player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ, false));
    }

    public String toString()
    {
        return "smite interval: " + interval + " smite";
    }

    public String getSyntax()
    {
        return "<interval>";
    }

}
