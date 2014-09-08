package com.forgeessentials.api.permissions;

import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

/**
 * {@link RootZone} is the root of the permission tree and has the lowest priority of all zones. It's purpose is to hold default permissions, which have been
 * set by {@link IPermissionsHelper#registerPermission()}
 * 
 * @author Bjoern Zeutzheim
 */
public class RootZone extends Zone {

	public RootZone()
	{
		super(0);
	}

	@Override
	public boolean isInZone(WorldPoint point)
	{
		return true;
	}

	@Override
	public boolean isInZone(WorldArea point)
	{
		return true;
	}

	@Override
	public boolean isPartOfZone(WorldArea point)
	{
		return true;
	}

	@Override
	public Zone getParent()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "_ROOT_";
	}

}
