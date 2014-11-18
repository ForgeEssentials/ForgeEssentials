package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commands.CommandAFK;
import com.forgeessentials.util.selections.WarpPoint;

public class AFKdata {
    public EntityPlayerMP player;
    public boolean needstowait;
    private int waittime;
    private long startTime;
    private WarpPoint lastPos;

    public AFKdata(EntityPlayerMP player)
    {
        this.player = player;
        waittime = CommandAFK.warmup;
        lastPos = new WarpPoint(player);
        needstowait = true;
        startTime = System.currentTimeMillis();
    }

    public void count()
    {
        if (player == null)
        {
            CommandsEventHandler.afkListToRemove.add(this);
            return;
        }

        if (!lastPos.equals(new WarpPoint(player)))
        {
            CommandAFK.instance.abort(this);
        }

        if (needstowait)
        {
            if ((System.currentTimeMillis() - startTime) / 1000L > waittime)
            {
                CommandAFK.instance.makeAFK(this);
                needstowait = false;
            }
        }
    }
}
