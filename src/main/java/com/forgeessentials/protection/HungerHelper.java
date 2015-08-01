package com.forgeessentials.protection;

import java.util.List;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionManager;

public class HungerHelper extends TimerTask
{
    @Override
    public void run()
    {
        for (EntityPlayerMP p : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList)
        {
            if (!PermissionManager.checkPermission(p, ModuleProtection.PERM_NEEDSFOOD))
            {
                p.getFoodStats().addStats(20, 1.0F);
            }
        }
    }
}
