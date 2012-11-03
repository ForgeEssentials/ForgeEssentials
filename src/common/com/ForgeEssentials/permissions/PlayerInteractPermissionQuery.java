package com.ForgeEssentials.permissions;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.HasResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Cancelable
@HasResult
/**
 * Basis of the ForgeEssentials Permissions system. All area-based permissions come through this class
 * as it contains a position, 
 * 
 * @author MysteriousAges
 *
 */
public class PlayerInteractPermissionQuery extends PlayerInteractEvent {

	public PlayerInteractPermissionQuery(EntityPlayer player,
			Action action, int x, int y, int z, int face) {
		super(player, action, x, y, z, face);
	}

}
