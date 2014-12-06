package com.forgeessentials.teleport.portal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * 
 */
public class PortalManager extends ServerEventHandler {

    private static PortalManager instance;

    protected Map<String, Portal> portals = new HashMap<>();

    public PortalManager()
    {
        super();
        instance = this;
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
                TeleportHelper.doTeleport((EntityPlayerMP) e.entityPlayer, new WarpPoint(portal.target, e.entityPlayer.rotationPitch,
                        e.entityPlayer.rotationYaw));
            }
        }
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent e)
    {
        if (e.phase == Phase.END)
            return;
        for (Portal portal : portals.values())
        {
            if (!portal.getPortalArea().isValid())
                continue;

            World world = DimensionManager.getWorld(portal.getPortalArea().getDimension());
            if (world == null)
                continue;

            // TODO: This is highly unefficient! Use some events instead!
            for (int ix = portal.getPortalArea().getLowPoint().getX(); ix <= portal.getPortalArea().getHighPoint().getX(); ix++)
            {
                for (int iy = portal.getPortalArea().getLowPoint().getY(); iy <= portal.getPortalArea().getHighPoint().getY(); iy++)
                {
                    for (int iz = portal.getPortalArea().getLowPoint().getZ(); iz <= portal.getPortalArea().getHighPoint().getZ(); iz++)
                    {
                        if (world.getBlock(ix, iy, iz) != Blocks.portal)
                        {
                            world.setBlock(ix, iy, iz, Blocks.portal);
                        }
                    }
                }
            }

            // world = Minecraft.getMinecraft().theWorld;
            // if (new Random().nextInt(100) < 100)
            // {
            // NamedWorldArea area = portal.getPortalArea();
            // Point start = area.getLowPoint();
            // Point size = area.getSize();
            // size.x++;
            // size.y++;
            // size.z++;
            // Random rnd = new Random();
            // world.spawnParticle("flame", start.getX() + rnd.nextFloat() *
            // size.getX(), start.getY() + rnd.nextFloat() * size.getY(),
            // start.getZ() + rnd.nextFloat() * size.getZ(), 0, 0, 0);
            // }
        }
    }

    public void remove(String name)
    {
        portals.remove(name);
        DataManager.getInstance().delete(Portal.class, name);
    }

    public void add(String name, Portal portal)
    {
        portals.put(name, portal);
        DataManager.getInstance().save(portal, name);
    }

}
