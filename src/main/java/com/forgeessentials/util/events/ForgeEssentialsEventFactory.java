package com.forgeessentials.util.events;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.UUID;

public class ForgeEssentialsEventFactory {
    // TICK STUFF

    private HashMap<UUID, WarpPoint> befores;

    public ForgeEssentialsEventFactory()
    {
        befores = new HashMap<UUID, WarpPoint>();
    }

    public static boolean onBlockPlace(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz)
    {
        // calculate offsets.
        ForgeDirection dir = ForgeDirection.getOrientation(side);

        x = +dir.offsetX;
        y = +dir.offsetY;
        z = +dir.offsetZ;

        PlayerBlockPlace ev = new PlayerBlockPlace(itemStack, player, world, x, y, z, side, hitx, hity, hitz);
        MinecraftForge.EVENT_BUS.post(ev);
        return !ev.isCanceled();
    }

    @SubscribeEvent
    public void handlePlayerMove(TickEvent.PlayerTickEvent e)
    {
    	if (!(e.player instanceof EntityPlayerMP))
    		return;
        EntityPlayerMP player = (EntityPlayerMP) e.player;

        WarpPoint before = befores.get(player.getPersistentID());
        WarpPoint current = new WarpPoint(player);

        // obviously.. if there IS no before.. don't worry about it.
        if (before == null)
        {
            befores.put(player.getPersistentID(), current);
            return;
        }

        // no respawn stuff or respawn stuff
        if (player.isDead || player.worldObj == null || before.dim != current.dim)
        {
            befores.remove(player.getPersistentID());
            return;
        }

        if (before.equals(current))
        {
            return;
        }

        PlayerMoveEvent event = new PlayerMoveEvent(player, before, current);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled())
        {
            FunctionHelper.setPlayer(player, before);
        }
        else
        {
            befores.put(player.getPersistentID(), current);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e)
    {
        befores.remove(e.player.getPersistentID());
    }

    // BLOCK STUFF

    // ZONE STUFF
    @SubscribeEvent
    public void playerMove(PlayerMoveEvent e)
    {
        Zone before = APIRegistry.zones.getWhichZoneIn(e.before);
        Zone after = APIRegistry.zones.getWhichZoneIn(e.after);

        if (before != after)
        {
            PlayerChangedZone event = new PlayerChangedZone(e.entityPlayer, before, after, e.before, e.after);
            MinecraftForge.EVENT_BUS.post(event);
            e.setCanceled(event.isCanceled());
        }
    }
}
