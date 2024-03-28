package com.forgeessentials.teleport.portal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.events.entity.EntityPortalEvent;
import com.forgeessentials.util.events.player.PlayerMoveEvent;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class PortalManager extends ServerEventHandler
{

    private static PortalManager instance;

    protected Map<String, Portal> portals = new HashMap<>();

    // private static boolean mixinLoaded = false;

    public static Block portalBlock = Blocks.NETHER_PORTAL;

    public PortalManager()
    {
        super();
        instance = this;
        /*
         * mixinLoaded = FEMixinConfig.getInjectedPatches().contains("block.MixinBlockPortal"); if (!mixinLoaded) { LoggingHandler.felog.
         * error("Unable to apply portal block mixin. Will revert to glass panes for portals." ); portalBlock = Blocks.glass_pane; }
         */
    }

    public static PortalManager getInstance()
    {
        return instance;
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        super.serverStopped(e);
        save();
    }

    public void load()
    {
        portals = DataManager.getInstance().loadAll(Portal.class);
        for (Portal portal : portals.values())
            buildPortalFrame(portal);
    }

    public void save()
    {
        for (Entry<String, Portal> portal : portals.entrySet())
            DataManager.getInstance().save(portal.getValue(), portal.getKey());
    }

    @SubscribeEvent
    public void playerMove(PlayerMoveEvent e)
    {
        WorldPoint after = e.after.toWorldPoint();
        WorldPoint before = e.before.toWorldPoint();
        for (Portal portal : portals.values())
        {
            if (portal.getPortalArea().contains(after) && !portal.getPortalArea().contains(before))
            {
                if (!MinecraftForge.EVENT_BUS.post(new EntityPortalEvent(e.getEntity(), after.getWorld(),
                        after.getBlockPos(), portal.target.getWorld(), portal.target.getBlockPos())))
                {
                    TeleportHelper.doTeleport((ServerPlayer) e.getPlayer(),
                            portal.target.toWarpPoint(e.getPlayer().getXRot(), e.getPlayer().getYRot()));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void breakEvent(BreakEvent event)
    {
        if (FMLEnvironment.dist.isClient())
            return;
        WorldPoint point = new WorldPoint(event.getPlayer().level, event.getPos());
        Portal portal = getPortalAt(point);
        if (portal != null && portal.hasFrame()) {
            event.setCanceled(true);
        }
    }

    public Portal getPortalAt(WorldPoint point)
    {
        for (Portal portal : portals.values())
            if (portal.getPortalArea().contains(point))
                return portal;
        return null;
    }

    // @SubscribeEvent
    // public void serverTick(ServerTickEvent e)
    // {
    // if (e.phase == Phase.END)
    // return;
    // for (Portal portal : portals.values())
    // {
    // if (!portal.getPortalArea().isValid())
    // continue;
    //
    // // WorldClient world = Minecraft.getMinecraft().theWorld;
    // // if (world.provider.getDimensionId() ==
    // portal.getPortalArea().getDimension())
    // // {
    // // if (new Random().nextInt(100) < 100)
    // // {
    // // NamedWorldArea area = portal.getPortalArea();
    // // Point start = area.getLowPoint();
    // // Point size = area.getSize();
    // // size.x++;
    // // size.y++;
    // // size.z++;
    // // Random rnd = new Random();
    // // world.spawnParticle("flame", start.getX() + rnd.nextFloat() *
    // size.getX(), start.getY() + rnd.nextFloat() * size.getY(),
    // // start.getZ() + rnd.nextFloat() * size.getZ(), 0, 0, 0);
    // // }
    // // }
    // }
    // }

    public Portal get(String name)
    {
        return portals.get(name);
    }

    public void remove(String name)
    {
        destroyPortalFrame(portals.remove(name));
        DataManager.getInstance().delete(Portal.class, name);
    }

    public void add(String name, Portal portal)
    {
        portals.put(name, portal);
        DataManager.getInstance().save(portal, name);
        buildPortalFrame(portal);
    }

    private static void buildPortalFrame(Portal portal)
    {
        if (!portal.hasFrame())
            return;
        Level world = ServerUtil.getWorldFromString(portal.getPortalArea().getDimension());
        if (world != null)
        {
            for (int ix = portal.getPortalArea().getLowPoint().getX(); ix <= portal.getPortalArea().getHighPoint()
                    .getX(); ix++)
                for (int iy = portal.getPortalArea().getLowPoint().getY(); iy <= portal.getPortalArea().getHighPoint()
                        .getY(); iy++)
                    for (int iz = portal.getPortalArea().getLowPoint().getZ(); iz <= portal.getPortalArea()
                            .getHighPoint().getZ(); iz++)
                    {
                        BlockPos pos = new BlockPos(ix, iy, iz);
                        if (world.getBlockState(pos).getBlock() != portalBlock)
                            world.setBlockAndUpdate(pos, portalBlock.defaultBlockState());
                    }
        }
    }

    private static void destroyPortalFrame(Portal portal)
    {
        if (!portal.hasFrame())
            return;
        Level world = ServerUtil.getWorldFromString(portal.getPortalArea().getDimension());
        if (world != null)
        {
            for (int ix = portal.getPortalArea().getLowPoint().getX(); ix <= portal.getPortalArea().getHighPoint()
                    .getX(); ix++)
                for (int iy = portal.getPortalArea().getLowPoint().getY(); iy <= portal.getPortalArea().getHighPoint()
                        .getY(); iy++)
                    for (int iz = portal.getPortalArea().getLowPoint().getZ(); iz <= portal.getPortalArea()
                            .getHighPoint().getZ(); iz++)
                    {
                        BlockPos pos = new BlockPos(ix, iy, iz);
                        Block block = world.getBlockState(pos).getBlock();
                        if (block == portalBlock || block == Blocks.NETHER_PORTAL)
                            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
        }
    }

}
