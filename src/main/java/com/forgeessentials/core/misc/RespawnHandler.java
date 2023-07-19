package com.forgeessentials.core.misc;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.util.PlayerInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RespawnHandler
{

    protected Set<ServerPlayerEntity> respawnPlayers = Collections
            .newSetFromMap(new WeakHashMap<ServerPlayerEntity, Boolean>());

    public RespawnHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static WarpPoint getSpawn(PlayerEntity player, WarpPoint location, boolean doDefaultSpawn)
    {
        UserIdent ident = UserIdent.get(player);
        String spawnProperty = APIRegistry.perms.getPermission(ident, location == null ? null : location.toWorldPoint(),
                null, GroupEntry.toList(APIRegistry.perms.getPlayerGroups(ident)), FEPermissions.SPAWN_LOC, true);
        if (spawnProperty != null)
        {
            WarpPoint point = WarpPoint.fromString(spawnProperty);
            // if (point == null)
            // {
            // WorldPoint worldPoint = WorldPoint.fromString(spawnProperty);
            // if (worldPoint != null)
            // point = new WarpPoint(worldPoint, player.cameraYaw, player.cameraPitch);
            // }
            if (point != null)
                return point;
        }
        if (doDefaultSpawn)
            return null;
        else
            return new WarpPoint(((ServerPlayerEntity) player).getRespawnDimension().location().toString(),
                    ((ServerPlayerEntity) player).getRespawnPosition(), player.xRot, player.yRot);
    }

    public static WarpPoint getSpawn(PlayerEntity player, WarpPoint location)
    {
        return getSpawn(player, location, true);
    }

    public static WarpPoint getPlayerSpawn(PlayerEntity player, WarpPoint location, boolean doDefaultSpawn)
    {
        UserIdent ident = UserIdent.get(player);

        boolean bedEnabled = APIRegistry.perms.checkUserPermission(ident, FEPermissions.SPAWN_BED);
        if (bedEnabled)
        {
            ServerPlayerEntity entity = (ServerPlayerEntity) player;
            BlockPos spawn = entity.getRespawnPosition();
            if (spawn != null)
            {
                // Bed seems OK, so just return null to let default MC code handle respawn
                if (doDefaultSpawn)
                    return null;
                return new WarpPoint(player.level.dimension().location().toString(), spawn, player.xRot, player.yRot);
            }
        }

        return getSpawn(player, location, doDefaultSpawn);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent e)
    {
        if (e.getEntityLiving() instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) e.getEntityLiving();
            PlayerInfo pi = PlayerInfo.get(player.getGameProfile().getId());
            pi.setLastDeathLocation(new WarpPoint(player));
            pi.setLastTeleportOrigin(pi.getLastDeathLocation());
        }
    }

    @SubscribeEvent
    public void doFirstRespawn(EntityJoinWorldEvent e)
    {
        if (!e.getEntity().getClass().equals(ServerPlayerEntity.class))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) e.getEntity();
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
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        File f = new File(event.getPlayerDirectory(), event.getPlayerUUID() + ".dat");
        if (!f.exists())
        {
            WarpPoint p = getPlayerSpawn(player, null, true);
            if (p != null)
            {
                if (!player.level.dimension().location().toString().equals(p.getDimension()))
                    respawnPlayers.add(player);
                else
                    player.moveTo(p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch());
            }
        }
    }

    @SubscribeEvent
    public void doRespawn(PlayerRespawnEvent event)
    {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        player.connection.player = player;

        WarpPoint lastDeathLocation = PlayerInfo.get(player.getGameProfile().getId()).getLastDeathLocation();
        if (lastDeathLocation == null)
            lastDeathLocation = new WarpPoint(player);

        WarpPoint p = getPlayerSpawn(player, lastDeathLocation, true);
        if (p != null)
            TeleportHelper.doTeleport(player, p);
    }

}
