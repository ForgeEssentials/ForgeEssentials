package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

/**
 * {@link RootZone} is the root of the permission tree and has the lowest priority of all zones. It's purpose is to hold default permissions, which have been
 * set by {@link IPermissionsHelper#registerPermission()}
 * 
 * @author Olee
 */
public class RootZone extends Zone {

	private ServerZone serverZone;

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
	public String getName()
	{
		return "_ROOT_";
	}

	@Override
	public Zone getParent()
	{
		return null;
	}

	public ServerZone getServerZone()
	{
		return serverZone;
	}

	public void setServerZone(ServerZone serverZone)
	{
		this.serverZone = serverZone;
		if (serverZone != null)
			serverZone.setRootZone(this);
	}

}
