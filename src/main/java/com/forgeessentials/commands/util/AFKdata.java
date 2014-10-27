package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commands.CommandAFK;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.selections.WorldPoint;

public class AFKdata {
    public EntityPlayerMP player;
    public boolean needstowait;
    private int waittime;
    private long startTime;
    private WorldPoint lastPos;
    private WorldPoint currentPos;

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

        currentPos = new WarpPoint(player);
        if (!lastPos.equals(currentPos))
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
