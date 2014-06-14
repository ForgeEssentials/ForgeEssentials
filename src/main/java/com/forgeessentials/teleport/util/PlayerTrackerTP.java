package com.forgeessentials.teleport.util;

import com.forgeessentials.teleport.CommandSetSpawn;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerTrackerTP implements IPlayerTracker {
    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        CommandSetSpawn.spawns.remove(player.username);
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        // send to spawn point
        WarpPoint p = CommandSetSpawn.spawns.get(player.username);
        if (p != null)
        {
            FunctionHelper.setPlayer((EntityPlayerMP) player, p);
            player.posX = p.xd;
            player.posY = p.yd;
            player.posZ = p.zd;
        }
        else
        {

        }
    }
}