package com.forgeessentials.auth.lists;

import com.forgeessentials.api.APIRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerTracker implements IPlayerTracker {

    public static String banned;
    public static String notvip;
    public static String notwhitelisted;
    public static boolean whitelist;
    public static int vipslots;
    public static int offset;
    public int counter;
    public int maxcounter;

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        maxcounter = FMLCommonHandler.instance().getMinecraftServerInstance().getMaxPlayers() - vipslots - offset;
        if (whitelist)
        {
            if (!APIRegistry.perms.checkPermAllowed(player, "ForgeEssentials.Auth.isWhiteListed"))
            {
                ((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer(notwhitelisted);
            }
        }
        if (APIRegistry.perms.checkPermAllowed(player, "ForgeEssentials.Auth.isVIP"))
        {
            return;
        }
        else if (counter == maxcounter)
        {
            ((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer(notvip);
        }
        else
        {
            counter = counter + 1;
        }

    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        counter = counter - 1;
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {

    }

}
