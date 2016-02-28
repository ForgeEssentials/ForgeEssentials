package com.forgeessentials.core.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class TeleportHelper extends ServerEventHandler
{

    public static class SimpleTeleporter extends Teleporter
    {

        public SimpleTeleporter(WorldServer world)
        {
            super(world);
        }

        @Override
        public boolean func_180620_b(Entity entity, float yaw)
        {
            int i = MathHelper.floor_double(entity.posX);
            int j = MathHelper.floor_double(entity.posY) - 1;
            int k = MathHelper.floor_double(entity.posZ);
            entity.setLocationAndAngles((double)i, (double)j, (double)k, entity.rotationYaw, 0.0F);
            return true;
        }

        @Override
        public void removeStalePortalLocations(long totalWorldTime)
        {
            /* do nothing */
        }

        @Override
        public void func_180266_a(Entity entity, float yaw)
        {
            func_180620_b(entity, yaw);
        }

    }

    public static class TeleportInfo
    {

        private EntityPlayerMP player;

        private long start;

        private int timeout;

        private WarpPoint point;

        private WarpPoint playerPos;

        public TeleportInfo(EntityPlayerMP player, WarpPoint point, int timeout)
        {
            this.point = point;
            this.timeout = timeout;
            this.start = System.currentTimeMillis();
            this.player = player;
            this.playerPos = new WarpPoint(player);
        }

        public boolean check()
        {
            if (playerPos.distance(new WarpPoint(player)) > 0.2)
            {
                ChatOutputHandler.chatWarning(player, "Teleport cancelled.");
                return true;
            }
            if (System.currentTimeMillis() - start < timeout)
            {
                return false;
            }
            checkedTeleport(player, point);
            ChatOutputHandler.chatConfirmation(player, "Teleported.");
            return true;
        }

    }

    public static final String TELEPORT_COOLDOWN = "fe.teleport.cooldown";
    public static final String TELEPORT_WARMUP = "fe.teleport.warmup";
    public static final String TELEPORT_CROSSDIM = "fe.teleport.crossdim";
    public static final String TELEPORT_FROM = "fe.teleport.from";
    public static final String TELEPORT_TO = "fe.teleport.to";

    private static Map<UUID, TeleportInfo> tpInfos = new HashMap<>();

    public static void teleport(EntityPlayerMP player, WarpPoint point) throws CommandException
    {
        if (point.getWorld() == null)
        {
            DimensionManager.initDimension(point.getDimension());
            if (point.getWorld() == null)
            {
                ChatOutputHandler.chatError(player, Translator.translate("Unable to teleport! Target dimension does not exist"));
                return;
            }
        }

        // Check permissions
        UserIdent ident = UserIdent.get(player);
        if (!APIRegistry.perms.checkPermission(player, TELEPORT_FROM))
            throw new TranslatedCommandException("You are not allowed to teleport from here.");
        if (!APIRegistry.perms.checkUserPermission(ident, point.toWorldPoint(), TELEPORT_TO))
            throw new TranslatedCommandException("You are not allowed to teleport to that location.");
        if (player.dimension != point.getDimension() && !APIRegistry.perms.checkUserPermission(ident, point.toWorldPoint(), TELEPORT_CROSSDIM))
            throw new TranslatedCommandException("You are not allowed to teleport across dimensions.");

        // Get and check teleport cooldown
        int teleportCooldown = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, TELEPORT_COOLDOWN), 0) * 1000;
        if (teleportCooldown > 0)
        {
            PlayerInfo pi = PlayerInfo.get(player);
            long cooldownDuration = (pi.getLastTeleportTime() + teleportCooldown) - System.currentTimeMillis();
            if (cooldownDuration >= 0)
            {
                ChatOutputHandler.chatNotification(player, Translator.format("Cooldown still active. %d seconds to go.", cooldownDuration / 1000));
                return;
            }
        }

        // Get and check teleport warmup
        int teleportWarmup = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(ident, TELEPORT_WARMUP), 0);
        if (teleportWarmup <= 0)
        {
            checkedTeleport(player, point);
            return;
        }

        if (!canTeleportTo(point))
        {
            ChatOutputHandler.chatError(player, Translator.translate("Unable to teleport! Target location obstructed."));
            return;
        }

        // Setup timed teleport
        tpInfos.put(player.getPersistentID(), new TeleportInfo(player, point, teleportWarmup * 1000));
        ChatOutputHandler.chatNotification(player,
                Translator.format("Teleporting. Please stand still for %s.", ChatOutputHandler.formatTimeDurationReadable(teleportWarmup, true)));
    }

    public static boolean canTeleportTo(WarpPoint point)
    {
        if (point.getY() < 0)
            return false;
        Block block1 = point.getWorld().getBlockState(point.getBlockPos()).getBlock();
        Block block2 = point.getWorld().getBlockState(new BlockPos(point.getBlockX(), point.getBlockY() + 1, point.getBlockZ())).getBlock();
        boolean block1Free = !block1.getMaterial().isSolid() || block1.getBlockBoundsMaxX() < 1 || block1.getBlockBoundsMaxY() > 0;
        boolean block2Free = !block2.getMaterial().isSolid() || block2.getBlockBoundsMaxX() < 1 || block2.getBlockBoundsMaxY() > 0;
        return block1Free && block2Free;
    }

    public static void checkedTeleport(EntityPlayerMP player, WarpPoint point)
    {
        if (!canTeleportTo(point))
        {
            ChatOutputHandler.chatError(player, Translator.translate("Unable to teleport! Target location obstructed."));
            return;
        }

        PlayerInfo pi = PlayerInfo.get(player);
        pi.setLastTeleportOrigin(new WarpPoint(player));
        pi.setLastTeleportTime(System.currentTimeMillis());
        pi.setLastDeathLocation(null);

        doTeleport(player, point);
    }

    public static void doTeleport(EntityPlayerMP player, WarpPoint point)
    {
        if (point.getWorld() == null)
        {
            LoggingHandler.felog.error("Error teleporting player. Target world is NULL");
            return;
        }
        // TODO: Handle teleportation of mounted entity
        player.mountEntity(null);

        if (player.dimension != point.getDimension())
        {
            SimpleTeleporter teleporter = new SimpleTeleporter(point.getWorld());
            transferPlayerToDimension(player, point.getDimension(), teleporter);
        }
        player.playerNetServerHandler.setPlayerLocation(point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
    }

    public static void doTeleportEntity(Entity entity, WarpPoint point)
    {
        if (entity instanceof EntityPlayerMP)
        {
            doTeleport((EntityPlayerMP) entity, point);
            return;
        }
        if (entity.dimension != point.getDimension())
            entity.travelToDimension(point.getDimension());
        entity.setLocationAndAngles(point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
    }

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        if (e.phase == TickEvent.Phase.START)
        {
            for (Iterator<TeleportInfo> it = tpInfos.values().iterator(); it.hasNext();)
            {
                TeleportInfo tpInfo = it.next();
                if (tpInfo.check())
                {
                    it.remove();
                }
            }
        }
    }

    public static void transferPlayerToDimension(EntityPlayerMP player, int dimension, Teleporter teleporter)
    {
        int oldDim = player.dimension;
        MinecraftServer mcServer = MinecraftServer.getServer();

        WorldServer oldWorld = mcServer.worldServerForDimension(player.dimension);
        player.dimension = dimension;
        WorldServer newWorld = mcServer.worldServerForDimension(player.dimension);
        player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, newWorld.getDifficulty(),
                newWorld.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType())); // Forge: Use new dimensions information
        oldWorld.removePlayerEntityDangerously(player);
        player.isDead = false;

        transferEntityToWorld(player, oldDim, oldWorld, newWorld, teleporter);

        mcServer.getConfigurationManager().func_72375_a(player, oldWorld);
        player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
                player.rotationPitch);
        player.theItemInWorldManager.setWorld(newWorld);
        mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, newWorld);
        mcServer.getConfigurationManager().syncPlayerInventory(player);
        Iterator<?> iterator = player.getActivePotionEffects().iterator();
        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect) iterator.next();
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potioneffect));
        }
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
    }

    public static void transferEntityToWorld(Entity entity, int oldDim, WorldServer oldWorld, WorldServer newWorld, Teleporter teleporter)
    {
        WorldProvider pOld = oldWorld.provider;
        WorldProvider pNew = newWorld.provider;
        double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
        double d0 = entity.posX * moveFactor;
        double d1 = entity.posZ * moveFactor;
        double d3 = entity.posX;
        double d4 = entity.posY;
        double d5 = entity.posZ;
        float f = entity.rotationYaw;
        d0 = MathHelper.clamp_int((int) d0, -29999872, 29999872);
        d1 = MathHelper.clamp_int((int) d1, -29999872, 29999872);
        if (entity.isEntityAlive())
        {
            entity.setLocationAndAngles(d0, entity.posY, d1, entity.rotationYaw, entity.rotationPitch);
            teleporter.func_180266_a(entity, f);
            newWorld.spawnEntityInWorld(entity);
            newWorld.updateEntityWithOptionalForce(entity, false);
        }
        entity.setWorld(newWorld);
    }

}
