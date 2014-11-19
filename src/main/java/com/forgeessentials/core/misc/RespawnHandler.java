package com.forgeessentials.core.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class RespawnHandler {

    public RespawnHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static WarpPoint getPlayerSpawn(EntityPlayer player, WarpPoint location)
    {
        UserIdent ident = new UserIdent(player);
        if (location == null)
            location = new WarpPoint(player);
        String spawnProperty = APIRegistry.perms.getPermission(ident, location.toWorldPoint(), null, APIRegistry.perms.getPlayerGroups(ident), FEPermissions.SPAWN, true);
        WorldPoint point = null;
        if (spawnProperty == null)
            return null;
        if (spawnProperty.equalsIgnoreCase("bed"))
        {
            if (player.getBedLocation() != null)
            {
                ChunkCoordinates spawn = player.getBedLocation();
                EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
                point = new WorldPoint(player.dimension, spawn.posX, spawn.posY, spawn.posZ);
            }
        }
        else
        {
            point = WorldPoint.fromString(spawnProperty);
        }
        if (point == null)
            return null;
        return new WarpPoint(point, player.cameraYaw, player.cameraPitch);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (e.entityLiving instanceof EntityPlayer)
        {
            EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
            PlayerInfo.getPlayerInfo(player.getPersistentID()).setLastDeathLocation(new WarpPoint(player));
        }
    }
    
    @SubscribeEvent
    public void doFirstRespawn(EntityJoinWorldEvent e)
    {
        if (e.entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) e.entity;
            if (!PlayerInfo.playerInfoExists(player.getPersistentID()))
            {
                WarpPoint p = getPlayerSpawn(player, null);
                if (p != null)
                {
                    FunctionHelper.teleportPlayer(player, p);
                    player.posX = p.getX();
                    player.posY = p.getY();
                    player.posZ = p.getZ();
                }
            }
        }
    }
    
    @SubscribeEvent
    public void doRespawn(PlayerRespawnEvent e)
    {
        WarpPoint lastDeathLocation = PlayerInfo.getPlayerInfo(e.player.getPersistentID()).getLastDeathLocation();
        if (lastDeathLocation == null)
            lastDeathLocation = new WarpPoint(e.player);
        
        WarpPoint p = getPlayerSpawn(e.player, lastDeathLocation);
        if (p != null)
        {
            FunctionHelper.teleportPlayer((EntityPlayerMP) e.player, p);
            e.player.posX = p.getX();
            e.player.posY = p.getY();
            e.player.posZ = p.getZ();
        }
    }

}
