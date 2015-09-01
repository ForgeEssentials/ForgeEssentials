package com.forgeessentials.core.misc;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class RespawnHandler
{

    protected Set<EntityPlayerMP> respawnPlayers = Collections.newSetFromMap(new WeakHashMap<EntityPlayerMP, Boolean>());

    public RespawnHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static WarpPoint getPlayerSpawn(EntityPlayer player, WarpPoint location, boolean doDefaultSpawn)
    {
        UserIdent ident = UserIdent.get(player);
        if (location == null)
            location = new WarpPoint(player);

        boolean bedEnabled = APIRegistry.perms.checkUserPermission(ident, FEPermissions.SPAWN_BED);
        if (bedEnabled)
        {
            ChunkCoordinates spawn = player.getBedLocation(player.dimension);
            if (spawn != null)
                spawn = EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
            if (spawn != null)
            {
                // Bed seems OK, so just return null to let default MC code handle respawn
                if (doDefaultSpawn)
                    return null;
                return new WarpPoint(player.dimension, spawn, player.cameraYaw, player.cameraPitch);
            }
        }

        String spawnProperty = APIRegistry.perms.getPermission(ident, location.toWorldPoint(), null,
                GroupEntry.toList(APIRegistry.perms.getPlayerGroups(ident)), FEPermissions.SPAWN_LOC, true);
        if (spawnProperty != null)
        {
            WorldPoint point = WorldPoint.fromString(spawnProperty);
            if (point != null)
                return new WarpPoint(point, player.cameraYaw, player.cameraPitch);
        }

        if (doDefaultSpawn)
            return null;
        else
            return new WarpPoint(player.dimension, player.worldObj.getSpawnPoint(), 0, 0);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (e.entityLiving instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) e.entityLiving;
            PlayerInfo pi = PlayerInfo.get(player.getPersistentID());
            pi.setLastDeathLocation(new WarpPoint(player));
            pi.setLastTeleportOrigin(pi.getLastDeathLocation());
        }
    }

    @SubscribeEvent
    public void doFirstRespawn(EntityJoinWorldEvent e)
    {
        if (!e.entity.getClass().equals(EntityPlayerMP.class))
            return;
        EntityPlayerMP player = (EntityPlayerMP) e.entity;
        if (respawnPlayers.remove(player))
        {
            WarpPoint p = getPlayerSpawn(player, null, true);
            if (p != null)
                TeleportHelper.doTeleport(player, p);
        }
    }

    @SubscribeEvent
    public void playerLoadFromFile(PlayerEvent.LoadFromFile event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
        File f = new File(event.playerDirectory, event.playerUUID + ".dat");
        if (!f.exists())
        {
            WarpPoint p = getPlayerSpawn(player, null, true);
            if (p != null)
            {
                if (player.dimension != p.getDimension())
                    respawnPlayers.add(player);
                else
                    player.setPositionAndRotation(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
            }
        }
    }

    @SubscribeEvent
    public void doRespawn(PlayerRespawnEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        player.playerNetServerHandler.playerEntity = player;

        WarpPoint lastDeathLocation = PlayerInfo.get(player.getPersistentID()).getLastDeathLocation();
        if (lastDeathLocation == null)
            lastDeathLocation = new WarpPoint(player);

        WarpPoint p = getPlayerSpawn(player, lastDeathLocation, true);
        if (p != null)
            TeleportHelper.doTeleport(player, p);
    }

}
