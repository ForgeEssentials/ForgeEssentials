package com.ForgeEssentials.permissions;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.HasResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@Cancelable
@HasResult
public class GenericPlayerInteractPermission extends PlayerInteractEvent {

	public GenericPlayerInteractPermission(EntityPlayer player, Action action,
			int x, int y, int z, int face) {
		super(player, action, x, y, z, face);
	}

}
