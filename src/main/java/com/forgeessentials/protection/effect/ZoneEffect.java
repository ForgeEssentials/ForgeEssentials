package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.EntityPlayerMP;

public abstract class ZoneEffect
{

    protected EntityPlayerMP player;

    protected int interval;

    protected long lastEffect;

    protected boolean lethal;

    public ZoneEffect(EntityPlayerMP player, int interval, boolean lethal)
    {
        this.player = player;
        this.interval = interval;
        this.lethal = lethal;
    }

    public void update()
    {
        if (interval >= 0 && System.currentTimeMillis() - lastEffect >= interval)
        {
            // Save last activation time and disable, if interval = 0 (once-only)
            lastEffect = System.currentTimeMillis();
            if (interval == 0)
                interval = -1;

            // Execute effect
            execute();
        }
    }

    public abstract void execute();

    public EntityPlayerMP getPlayer()
    {
        return player;
    }

    public int getInterval()
    {
        return interval;
    }

    public long getLastEffect()
    {
        return lastEffect;
    }

    public boolean isLethal()
    {
        return lethal;
    }

}
