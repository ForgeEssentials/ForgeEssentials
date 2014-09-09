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
@SaveableObject
public class RootZone extends Zone {

	@SaveableField
	private ServerZone serverZone;

	@SaveableField
	private int maxZoneID;
	
	//private List<PlayerData> players;
	
	private List<Group> groups = new ArrayList<Group>();

	public RootZone()
	{
		super(0);
		maxZoneID = 2;
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

	void setServerZone(ServerZone serverZone)
	{
		this.serverZone = serverZone;
	}


	public int getCurrentZoneID()
	{
		return maxZoneID;
	}

	public int getNextZoneID()
	{
		return maxZoneID++;
	}


}
