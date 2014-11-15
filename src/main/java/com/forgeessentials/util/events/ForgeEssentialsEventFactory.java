package com.forgeessentials.util.events;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.commons.selections.WarpPoint;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.UUID;

public class ForgeEssentialsEventFactory {
	// TICK STUFF

	private HashMap<UUID, WarpPoint> befores;

    private static ForgeEssentialsEventFactory INSTANCE;

	public ForgeEssentialsEventFactory()
	{
		befores = new HashMap<UUID, WarpPoint>();
        INSTANCE = this;
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
		if (player.isDead || player.worldObj == null || before.getDimension() != current.getDimension())
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
			FunctionHelper.teleportPlayer(player, before);
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

	// ZONE STUFF
	@SubscribeEvent
	public void playerMove(PlayerMoveEvent e)
	{
		Zone before = APIRegistry.perms.getZonesAt(e.before).get(0);
		Zone after = APIRegistry.perms.getZonesAt(e.after).get(0);
		if (!before.equals(after))
		{
			PlayerChangedZone event = new PlayerChangedZone(e.entityPlayer, before, after, e.before, e.after);
			MinecraftForge.EVENT_BUS.post(event);
			e.setCanceled(event.isCanceled());
		}
	}

    public static ForgeEssentialsEventFactory getInstance()
    {
        return INSTANCE;
    }
}
