package com.forgeessentials.api.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import net.minecraft.entity.player.EntityPlayer;

/**
 * {@link ServerZone} contains every player on the whole server. Has second lowest priority with next being {@link RootZone}.
 * 
 * @author Bjoern Zeutzheim
 */
public class ServerZone extends Zone {

	private RootZone rootZone;

	public ServerZone(RootZone rootZone)
	{
		super(1);
		this.rootZone = rootZone;
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
		return rootZone;
	}

	@Override
	public String getName()
	{
		return "_SERVER_";
	}

}
