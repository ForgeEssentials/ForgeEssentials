package com.forgeessentials.api.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.server.MinecraftServer;

import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link ServerZone} contains every player on the whole server. Has second lowest priority with next being {@link RootZone}.
 * 
 * @author Olee
 */
public class ServerZone extends Zone {

	private RootZone rootZone;

	private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

	private int maxZoneID;

	private Map<UserIdent, Set<String>> playerGroups = new HashMap<UserIdent, Set<String>>();

	private Set<UserIdent> knownPlayers = new HashSet<UserIdent>();

	// ------------------------------------------------------------

	public ServerZone()
	{
		super(1);
		setGroupPermission(IPermissionsHelper.GROUP_DEFAULT, FEPermissions.GROUP, true);
		setGroupPermission(IPermissionsHelper.GROUP_GUESTS, FEPermissions.GROUP, true);
		setGroupPermission(IPermissionsHelper.GROUP_OPERATORS, FEPermissions.GROUP, true);
		setGroupPermissionProperty(IPermissionsHelper.GROUP_DEFAULT, FEPermissions.GROUP_PRIORITY, "0");
		setGroupPermissionProperty(IPermissionsHelper.GROUP_GUESTS, FEPermissions.GROUP_PRIORITY, "1");
		setGroupPermissionProperty(IPermissionsHelper.GROUP_OPERATORS, FEPermissions.GROUP_PRIORITY, "2");
		setGroupPermissionProperty(IPermissionsHelper.GROUP_GUESTS, FEPermissions.PREFIX, "[GUEST]");
		setGroupPermissionProperty(IPermissionsHelper.GROUP_OPERATORS, FEPermissions.PREFIX, "[OPERATOR]");
	}

	public ServerZone(RootZone rootZone)
	{
		this();
		this.maxZoneID = 1;
		this.rootZone = rootZone;
		this.rootZone.setServerZone(this);
	}

	// ------------------------------------------------------------

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
		return "_SERVER_";
	}

	@Override
	public Zone getParent()
	{
		return rootZone;
	}

	@Override
	public ServerZone getServerZone()
	{
		return this;
	}

	public RootZone getRootZone()
	{
		return rootZone;
	}
	
	// ------------------------------------------------------------

	public Map<Integer, WorldZone> getWorldZones()
	{
		return worldZones;
	}

	public void addWorldZone(WorldZone zone)
	{
		worldZones.put(zone.getDimensionID(), zone);
		setDirty();
	}

	public int getNextZoneID()
	{
		return maxZoneID;
	}

	public int nextZoneID()
	{
		return ++maxZoneID;
	}

	public void setMaxZoneId(int maxId)
	{
		this.maxZoneID = maxId;
	}

	void setRootZone(RootZone rootZone)
	{
		this.rootZone = rootZone;
	}

	// ------------------------------------------------------------

	public Set<String> getGroups()
	{
		return getGroupPermissions().keySet();
	}

	public boolean groupExists(String name)
	{
		return getGroupPermissions().containsKey(name);
	}

	// ------------------------------------------------------------

	public void addPlayerToGroup(UserIdent ident, String group)
	{
		Set<String> groupSet = playerGroups.get(ident);
		if (groupSet == null)
		{
			groupSet = new TreeSet<String>();
			playerGroups.put(ident, groupSet);
		}
		groupSet.add(group);
		setDirty();
	}

	public void removePlayerFromGroup(UserIdent ident, String group)
	{
		Set<String> groupSet = playerGroups.get(ident);
		if (groupSet != null)
			groupSet.remove(group);
		setDirty();
	}

	public Set<String> getPlayerGroups(UserIdent ident)
	{
		Set<String> pgs = playerGroups.get(ident);
		HashSet result = pgs == null ? new HashSet<String>() : new HashSet<String>(pgs);
		if (ident != null && ident.hasPlayer() && MinecraftServer.getServer().getConfigurationManager().func_152596_g(ident.getPlayer().getGameProfile()))
		{
			result.add(IPermissionsHelper.GROUP_OPERATORS);
		}
		if (result.isEmpty())
		{
			result.add(IPermissionsHelper.GROUP_GUESTS);
		}
		// TODO: Sort groups by priority
		return result;
	}

	public Map<UserIdent, Set<String>> getPlayerGroups()
	{
		return playerGroups;
	}

	public String getPrimaryPlayerGroup(UserIdent ident)
	{
		Iterator<String> it = getPlayerGroups(ident).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}

	// ------------------------------------------------------------

	public void registerPlayer(UserIdent ident)
	{
		knownPlayers.add(ident);
	}

	public Set<UserIdent> getKnownPlayers()
	{
		return knownPlayers;
	}

}
