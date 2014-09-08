package com.forgeessentials.permissions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
import net.minecraftforge.permissions.api.context.EntityContext;
import net.minecraftforge.permissions.api.context.IContext;
import net.minecraftforge.permissions.api.context.PlayerContext;
import net.minecraftforge.permissions.api.context.TileEntityContext;
import net.minecraftforge.permissions.api.context.WorldContext;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.GlobalZone;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;
import com.mojang.authlib.GameProfile;

public class ZonedPermissionHelper implements IPermissionsHelper {

	private RootZone rootZone;

	private GlobalZone globalZone;

	private Map<Integer, Zone> zones = new HashMap<Integer, Zone>();

	private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

	private Map<String, Group> groups = new HashMap<String, Group>();

	private int maxZoneID;

	// ------------------------------------------------------------

	public ZonedPermissionHelper()
	{
		// MinecraftForge.EVENT_BUS.register(this);
		clear();
	}

	public void clear() {
		rootZone = null;
		globalZone = null;
		maxZoneID = 2;

		addZone(new RootZone());
		addZone(new GlobalZone(rootZone));

		// for (World world : DimensionManager.getWorlds())
		// {
		// getWorldZone(world);
		// }

		// TODO: TESTING
		globalZone.setGroupPermission(DEFAULT_GROUP, "fe.commands.gamemode", false);
		globalZone.setGroupPermission(DEFAULT_GROUP, "fe.commands.time", true);

		WorldZone world0 = getWorldZone(0);
		world0.setGroupPermission(DEFAULT_GROUP, "fe.commands.gamemode", true);
		world0.setGroupPermission(DEFAULT_GROUP, "fe.commands.time", false);
	}

	public Set<String> enumAllPermissions() {
		Set<String> perms = new TreeSet<String>();
		for (Zone zone : zones.values())
		{
			for (Map<String,String> groupPerms : zone.getGroups())
			{
				for (String perm : groupPerms.keySet())
				{
					perms.add(perm);
				}
			}
			for (Map<String,String> groupPerms : zone.getPlayers())
			{
				for (String perm : groupPerms.keySet())
				{
					perms.add(perm);
				}
			}
		}
		return perms;
	}

	// ------------------------------------------------------------

	/**
	 * Main function for permission retrieval. This method should not be used directly. Use the helper methods instead.
	 * 
	 * @param playerId
	 * @param point
	 * @param groups
	 * @param permissionNode
	 * @param isProperty
	 * @return
	 */
	public String getPermission(String playerId, WorldPoint point, WorldArea area, Collection<String> groups, String permissionNode, boolean isProperty)
	{
		// Get world zone
		WorldZone worldZone = worldZones.get(point.getDimension());

		// Get zones in correct order
		List<Zone> zones = new ArrayList<Zone>();
		if (worldZone != null)
		{
			for (Zone zone : worldZone.getAreaZones())
			{
				if (point != null && zone.isInZone(point) || area != null && zone.isInZone(area))
				{
					zones.add(zone);
				}
			}
			zones.add(worldZone);
		}
		zones.add(globalZone);
		zones.add(rootZone);

		return getPermission(zones, playerId, groups, permissionNode, isProperty);
	}

	public String getPermission(List<Zone> zones, String playerId, Collection<String> groups, String permissionNode, boolean isProperty)
	{
		// Add default group
		if (groups == null)
		{
			groups = new ArrayList<String>();
		}
		groups.add(DEFAULT_GROUP);

		// Build node list
		List<String> nodes = new ArrayList<String>();
		nodes.add(permissionNode);
		if (!isProperty)
		{
			String[] nodeParts = permissionNode.split("\\.");
			for (int i = nodeParts.length; i >= 0; i--)
			{
				String node = "";
				for (int j = 0; j < i; j++)
				{
					node += nodeParts[j] + ".";
				}
				nodes.add(node + PERMISSION_ASTERIX);
			}
			nodes.add(PERMISSION_ASTERIX);
		}

		// Check player permissions
		if (playerId != null)
		{
			for (String node : nodes)
			{
				for (Zone zone : zones)
				{
					String result = zone.getPlayerPermission(playerId, node);
					if (result != null)
					{
						return result;
					}
				}
			}
		}

		// Check group permissions
		for (String group : groups)
		{
			for (String node : nodes)
			{
				// Check group permissions
				for (Zone zone : zones)
				{
					String result = zone.getGroupPermission(group, node);
					if (result != null)
					{
						return result;
					}
				}
			}
		}

		return null;
	}

	// ------------------------------------------------------------

	@Override
	public void registerPermissionProperty(String permissionNode, String defaultValue)
	{
		rootZone.setGroupPermissionProperty(DEFAULT_GROUP, permissionNode, defaultValue);
	}

	@Override
	public void registerPermission(String permissionNode, PermissionsManager.RegisteredPermValue permLevel)
	{
		if (permLevel == RegisteredPermValue.FALSE)
			rootZone.setGroupPermission(DEFAULT_GROUP, permissionNode, false);
		else if (permLevel == RegisteredPermValue.TRUE)
			rootZone.setGroupPermission(DEFAULT_GROUP, permissionNode, true);
		else if (permLevel == RegisteredPermValue.OP)
		{
			rootZone.setGroupPermission(DEFAULT_GROUP, permissionNode, false);
			rootZone.setGroupPermission(OP_GROUP, permissionNode, true);
		}
	}

	@Override
	public void setPlayerPermission(String uuid, String permissionNode, boolean value)
	{
		globalZone.setPlayerPermission(uuid, permissionNode, value);
	}

	@Override
	public void setPlayerPermissionProperty(String uuid, String permissionNode, String value)
	{
		globalZone.setPlayerPermissionProperty(uuid, permissionNode, value);
	}

	@Override
	public void setGroupPermission(String group, String permissionNode, boolean value)
	{
		globalZone.setGroupPermission(group, permissionNode, value);
	}

	@Override
	public void setGroupPermissionProperty(String group, String permissionNode, String value)
	{
		globalZone.setGroupPermissionProperty(group, permissionNode, value);
	}

	// ------------------------------------------------------------
	// -- IPermissionProvider
	// ------------------------------------------------------------

	@Override
	public String getName()
	{
		return "ForgeEssentials";
	}

	@Override
	public boolean checkPerm(EntityPlayer player, String node, Map<String, IContext> contextInfo)
	{
		return checkPermission(player, node);
	}

	public static final IContext GLOBAL = new IContext()
	{
	};

	@Override
	public IContext getDefaultContext(EntityPlayer player)
	{
		IContext context = new PlayerContext(player);
		return context;
	}

	@Override
	public IContext getDefaultContext(TileEntity te)
	{
		return new TileEntityContext(te);
	}

	@Override
	public IContext getDefaultContext(ILocation loc)
	{
		return new net.minecraftforge.permissions.api.context.Point(loc);
	}

	@Override
	public IContext getDefaultContext(Entity entity)
	{
		return new EntityContext(entity);
	}

	@Override
	public IContext getDefaultContext(World world)
	{
		return new WorldContext(world);
	}

	@Override
	public IContext getGlobalContext()
	{
		return GLOBAL;
	}

	@Override
	public IContext getDefaultContext(Object whoKnows)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------
	// -- Zones
	// ------------------------------------------------------------

	@Override
	public int getNextZoneID()
	{
		return maxZoneID++;
	}

	@Override
	public Collection<Zone> getZones()
	{
		return zones.values();
	}

	@Override
	public Zone getZoneById(int id)
	{
		return zones.get(id);
	}

	@Override
	public Zone getZoneById(String id)
	{
		try
		{
			return getZoneById(Integer.parseInt(id));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	@Override
	public Collection<WorldZone> getWorldZones()
	{
		return worldZones.values();
	}

	@Override
	public List<Zone> getZonesAt(WorldPoint worldPoint)
	{
		WorldZone w = getWorldZone(worldPoint.getDimension());
		List<Zone> result = new ArrayList<Zone>();
		for (AreaZone zone : w.getAreaZones())
			if (zone.isInZone(worldPoint))
				result.add(zone);
		result.add(w);
		result.add(w.getParent());
		return result;
	}

	@Override
	public List<AreaZone> getAreaZonesAt(WorldPoint worldPoint)
	{
		WorldZone w = getWorldZone(worldPoint.getDimension());
		List<AreaZone> result = new ArrayList<AreaZone>();
		for (AreaZone zone : w.getAreaZones())
			if (zone.isInZone(worldPoint))
				result.add(zone);
		return result;
	}

	@Override
	public Zone getZoneAt(WorldPoint worldPoint)
	{
		List<Zone> zones = getZonesAt(worldPoint);
		return zones.isEmpty() ? null : zones.get(0);
	}

	@Override
	public AreaZone getAreaZoneAt(WorldPoint worldPoint)
	{
		List<AreaZone> zones = getAreaZonesAt(worldPoint);
		return zones.isEmpty() ? null : zones.get(0);
	}

	// ------------------------------------------------------------

	public RootZone getRootZone()
	{
		return rootZone;
	}

	@Override
	public GlobalZone getGlobalZone()
	{
		return globalZone;
	}

	@Override
	public WorldZone getWorldZone(int dimensionId)
	{
		WorldZone zone = worldZones.get(dimensionId);
		if (zone == null)
		{
			zone = new WorldZone(globalZone, dimensionId, getNextZoneID());
			addZone(zone);
		}
		return zone;
	}

	@Override
	public WorldZone getWorldZone(World world)
	{
		return getWorldZone(world.provider.dimensionId);
	}

	protected Zone addZone(Zone zone)
	{
		zones.put(zone.getId(), zone);
		if (zone instanceof RootZone)
			rootZone = (RootZone) zone;
		else if (zone instanceof GlobalZone)
			globalZone = (GlobalZone) zone;
		else if (zone instanceof WorldZone)
			worldZones.put(((WorldZone) zone).getDimensionID(), (WorldZone) zone);
		return zone;
	}
	
	// ------------------------------------------------------------
	// -- Group / Player
	// ------------------------------------------------------------

	@Override
	public String getPlayerUUID(EntityPlayer player)
	{
		return player.getGameProfile().getId().toString();
	}

	@Override
	public Group getGroup(String name)
	{
		return groups.get(name);
	}

	@Override
	public Collection<Group> getGroups()
	{
		return groups.values();
	}

	@Override
	public Group getPrimaryGroup(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getPlayerGroups(EntityPlayer player)
	{
		List<Group> groups = new ArrayList<Group>();
		// TODO: getPlayerGroups !!!!
		if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile()))
			groups.add(new Group(OP_GROUP, "", "", "", 0));
		return groups;
	}

	public List<String> getPlayerGroupNames(EntityPlayer player)
	{
		List<Group> groups = getPlayerGroups(player);
		List<String> names = new ArrayList<String>();
		for (Group group : groups)
		{
			names.add(group.getName());
		}
		return names;
	}

	// ------------------------------------------------------------
	// -- Permission checking
	// ------------------------------------------------------------

	protected boolean checkPermission(String permissionValue)
	{
		if (permissionValue == null)
		{
			return true;
		}
		else
		{
			return !permissionValue.equals(PERMISSION_FALSE);
		}
	}

	@Override
	public boolean checkPermission(EntityPlayer player, String permissionNode)
	{
		return checkPermission(getPermission(getPlayerUUID(player), new WorldPoint(player), null, getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, String permissionNode)
	{
		return getPermission(getPlayerUUID(player), new WorldPoint(player), null, getPlayerGroupNames(player), permissionNode, true);
	}

	@Override
	public Integer getPermissionPropertyInt(EntityPlayer player, String permissionNode)
	{
		String value = getPermissionProperty(player, permissionNode);
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, WorldPoint targetPoint, String permissionNode)
	{
		return checkPermission(getPermission(getPlayerUUID(player), targetPoint, null, getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, WorldPoint targetPoint, String permissionNode)
	{
		return getPermission(getPlayerUUID(player), targetPoint, null, getPlayerGroupNames(player), permissionNode, true);
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, WorldArea targetArea, String permissionNode)
	{
		return checkPermission(getPermission(getPlayerUUID(player), null, targetArea, getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, WorldArea targetArea, String permissionNode)
	{
		return getPermission(getPlayerUUID(player), null, targetArea, getPlayerGroupNames(player), permissionNode, true);
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, Zone zone, String permissionNode)
	{
		List<Zone> zones = new ArrayList<Zone>();
		zones.add(zone);
		zones.add(globalZone);
		zones.add(rootZone);
		return checkPermission(getPermission(zones, getPlayerUUID(player), getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, Zone zone, String permissionNode)
	{
		List<Zone> zones = new ArrayList<Zone>();
		zones.add(zone);
		zones.add(globalZone);
		zones.add(rootZone);
		return getPermission(zones, getPlayerUUID(player), getPlayerGroupNames(player), permissionNode, true);
	}

	// ------------------------------------------------------------
}
