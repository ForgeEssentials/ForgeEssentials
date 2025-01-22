package com.forgeessentials.teleport.portal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fe.event.entity.EntityPortalEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;





/**
 *
 */
public class PortalManager extends ServerEventHandler
{

    private static PortalManager instance;

    protected Map<String, Portal> portals = new HashMap<>();

    // private static boolean mixinLoaded = false;

    public static Block portalBlock = Blocks.portal;

    public PortalManager()
    {
        super();
        instance = this;
        /*
         * mixinLoaded = FEMixinConfig.getInjectedPatches().contains("block.MixinBlockPortal"); if (!mixinLoaded) {
         * LoggingHandler.felog.error("Unable to apply portal block mixin. Will revert to glass panes for portals."); portalBlock = Blocks.glass_pane; }
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
            	if (!MinecraftForge.EVENT_BUS.post(
                        new EntityPortalEvent(e.entity, after.getWorld(), after.getBlockPos(), portal.target.getDimension(), portal.target.getBlockPos())))
                {
                    TeleportHelper.doTeleport((EntityPlayerMP) e.entityPlayer,
                            portal.target.toWarpPoint(e.entityPlayer.rotationPitch, e.entityPlayer.rotationYaw));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void breakEvent(BreakEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        WorldPoint point = new WorldPoint(e.getPlayer().dimension, e.pos);
        Portal portal = getPortalAt(point);
        if (portal != null && portal.hasFrame())
            e.setCanceled(true);
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
    // // if (world.provider.dimensionId ==
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
        World world = DimensionManager.getWorld(portal.getPortalArea().getDimension());
        if (world != null)
        {
            for (int ix = portal.getPortalArea().getLowPoint().getX(); ix <= portal.getPortalArea().getHighPoint().getX(); ix++)
                for (int iy = portal.getPortalArea().getLowPoint().getY(); iy <= portal.getPortalArea().getHighPoint().getY(); iy++)
                    for (int iz = portal.getPortalArea().getLowPoint().getZ(); iz <= portal.getPortalArea().getHighPoint().getZ(); iz++)
                    {
                        BlockPos pos = new BlockPos(ix, iy, iz);
                        if (world.getBlockState(pos).getBlock() != portalBlock)
                            world.setBlockState(pos, portalBlock.getDefaultState());
                    }
        }
    }

    private static void destroyPortalFrame(Portal portal)
    {
        if (!portal.hasFrame())
            return;
        World world = DimensionManager.getWorld(portal.getPortalArea().getDimension());
        if (world != null)
        {
            for (int ix = portal.getPortalArea().getLowPoint().getX(); ix <= portal.getPortalArea().getHighPoint().getX(); ix++)
                for (int iy = portal.getPortalArea().getLowPoint().getY(); iy <= portal.getPortalArea().getHighPoint().getY(); iy++)
                    for (int iz = portal.getPortalArea().getLowPoint().getZ(); iz <= portal.getPortalArea().getHighPoint().getZ(); iz++)
                    {
                        BlockPos pos = new BlockPos(ix, iy, iz);
                        Block block = world.getBlockState(pos).getBlock();
                        if (block == portalBlock || block == Blocks.portal)
                            world.setBlockState(pos, Blocks.air.getDefaultState());
                    }
        }
    }

}
