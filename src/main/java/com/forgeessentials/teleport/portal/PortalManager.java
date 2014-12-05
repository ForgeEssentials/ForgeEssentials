package com.forgeessentials.teleport.portal;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.util.NamedWorldArea;
import com.forgeessentials.util.NamedWorldPoint;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * 
 * 
 * @author Olee
 */
public class PortalManager extends ServerEventHandler {

    protected Map<Integer, Portal> portals = new HashMap<>();

    public PortalManager()
    {
        portals.put(0, new Portal(new NamedWorldArea(0, new Point(0, 74, 0), new Point(4, 77, 0)), new NamedWorldPoint(0, 3, 74, 5)));
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
                TeleportHelper.doTeleport((EntityPlayerMP) e.entityPlayer, new WarpPoint(portal.target, e.entityPlayer.rotationPitch, e.entityPlayer.rotationYaw));
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
                        if (world.getBlock(ix, iy, iz) != Blocks.glass_pane)
                        {
                            world.setBlock(ix, iy, iz, Blocks.glass_pane);
                        }
                    }
                }
            }

//            world = Minecraft.getMinecraft().theWorld;
//            if (new Random().nextInt(100) < 100)
//            {
//                NamedWorldArea area = portal.getPortalArea();
//                Point start = area.getLowPoint();
//                Point size = area.getSize();
//                size.x++;
//                size.y++;
//                size.z++;
//                Random rnd = new Random();
//                world.spawnParticle("flame", start.getX() + rnd.nextFloat() * size.getX(), start.getY() + rnd.nextFloat() * size.getY(), start.getZ() + rnd.nextFloat() * size.getZ(), 0, 0, 0);
//            }
        }
    }

}
