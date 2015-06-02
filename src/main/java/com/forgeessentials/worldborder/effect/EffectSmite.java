package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectSmite extends WorldBorderEffect
{

    public int interval = 5000;

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
            pi.startTimeout(this.getClass().getName(), interval);
        }
    }

    public void doEffect(EntityPlayerMP player)
    {
        player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
    }

}
