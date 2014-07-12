package com.forgeessentials.commands.util;

import com.forgeessentials.commands.CommandAFK;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.AreaSelector.WorldPoint;
import net.minecraft.entity.player.EntityPlayerMP;

public class AFKdata {
    public EntityPlayerMP player;
    public boolean needstowait;
    int waittime;
    private WorldPoint lastPos;
    private WorldPoint currentPos;

    public AFKdata(EntityPlayerMP player)
    {
        this.player = player;
        waittime = CommandAFK.warmup;
        lastPos = new WarpPoint(player);
        needstowait = true;
    }

    public void count()
    {
        if (player == null)
        {
            EventHandler.afkListToRemove.add(this);
            return;
        }

        currentPos = new WarpPoint(player);
        if (!lastPos.equals(currentPos))
        {
            CommandAFK.instance.abort(this);
        }

        if (needstowait)
        {
            if (waittime == 0)
            {
                CommandAFK.instance.makeAFK(this);
            }
            waittime--;
        }
    }
}
