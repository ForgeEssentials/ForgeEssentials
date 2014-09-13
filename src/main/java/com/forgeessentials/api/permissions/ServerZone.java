package com.forgeessentials.api.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

	private Group defaultGroup = new Group(IPermissionsHelper.GROUP_DEFAULT, null, null, null, 0, 0);

	private Group guestGroup = new Group(IPermissionsHelper.GROUP_GUESTS, "[GUEST] ", null, null, 1, 1);

	private Group operatorGroup = new Group(IPermissionsHelper.GROUP_OPERATORS, "[OPERATOR] ", null, null, 2, 2);

	private Map<String, Group> groups = new HashMap<String, Group>();

	private Map<UserIdent, Set<Group>> playerGroups = new HashMap<UserIdent, Set<Group>>();

	private Set<UserIdent> knownPlayers = new HashSet<UserIdent>();

	// ------------------------------------------------------------

	public ServerZone()
	{
		super(1);
		groups.put(operatorGroup.getName(), operatorGroup);
		groups.put(defaultGroup.getName(), defaultGroup);
		groups.put(guestGroup.getName(), guestGroup);
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

	// ------------------------------------------------------------

	public RootZone getRootZone()
	{
		return rootZone;
	}

	public Map<Integer, WorldZone> getWorldZones()
	{
		return worldZones;
	}

	public void addWorldZone(WorldZone zone)
	{
		worldZones.put(zone.getDimensionID(), zone);
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

	public Map<String, Group> getGroups()
	{
		return this.groups;
	}

	public Group getDefaultGroup()
	{
		return defaultGroup;
	}

	public Group getGuestGroup()
	{
		return guestGroup;
	}

	public Group getOperatorGroup()
	{
		return operatorGroup;
	}

	public Group getGroup(String name)
	{
		return groups.get(name.toLowerCase());
	}

	public Group createGroup(String name)
	{
		if (groups.containsKey(name))
			return null;
		Group group = new Group(name);
		groups.put(group.getName().toLowerCase(), group);
		return group;
	}

	// ------------------------------------------------------------

	public void addPlayerToGroup(UserIdent ident, Group group)
	{
		Set<Group> groupSet = playerGroups.get(ident);
		if (groupSet == null)
		{
			groupSet = new TreeSet<Group>();
			playerGroups.put(ident, groupSet);
		}
		groupSet.add(group);
	}

	public void removePlayerFromGroup(UserIdent ident, Group group)
	{
		Set<Group> groupSet = playerGroups.get(ident);
		if (groupSet != null)
			groupSet.remove(group);
	}

	public Set<Group> getPlayerGroups(UserIdent ident)
	{
		Set pgs = playerGroups.get(ident);
		HashSet result = pgs == null ? new HashSet<Group>() : new HashSet<Group>(pgs);
		if (ident.hasPlayer() && MinecraftServer.getServer().getConfigurationManager().func_152596_g(ident.getPlayer().getGameProfile()))
		{
			result.add(getServerZone().getOperatorGroup());
		}
		if (groups.isEmpty())
		{
			result.add(getServerZone().getGuestGroup());
		}
		return result;
	}

	public Group getPrimaryPlayerGroup(UserIdent ident)
	{
		Iterator<Group> it = getPlayerGroups(ident).iterator();
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
