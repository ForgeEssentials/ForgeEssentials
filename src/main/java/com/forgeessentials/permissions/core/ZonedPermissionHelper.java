package com.forgeessentials.permissions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.permissions.IContext;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * 
 * @author Olee
 */
public class ZonedPermissionHelper implements IPermissionsHelper {

	// TODO: Persist this field
	private RootZone rootZone;

	private Map<Integer, Zone> zones = new HashMap<Integer, Zone>();

	private Map<String, Group> groups = new HashMap<String, Group>();

	private IZonePersistenceProvider persistenceProvider;

	// ------------------------------------------------------------

	public ZonedPermissionHelper()
	{
		DataStorageManager.registerSaveableType(new ClassContainer(true, Zone.PermissionList.class, String.class, String.class));
		DataStorageManager.registerSaveableType(new ClassContainer(Zone.class));
		DataStorageManager.registerSaveableType(new ClassContainer(RootZone.class));
		DataStorageManager.registerSaveableType(new ClassContainer(ServerZone.class));
		DataStorageManager.registerSaveableType(new ClassContainer(WorldZone.class));
		DataStorageManager.registerSaveableType(new ClassContainer(AreaZone.class));

		FMLCommonHandler.instance().bus().register(this);

		rootZone = new RootZone();
		clear();
	}

	// ------------------------------------------------------------
	// -- Persistence
	// ------------------------------------------------------------

	public void clear()
	{
		zones.clear();
		addZone(rootZone);
		addZone(new ServerZone(rootZone));

		// for (World world : DimensionManager.getWorlds())
		// {
		// getWorldZone(world);
		// }

		// getServerZone().setGroupPermission(DEFAULT_GROUP, "fe.commands.gamemode", false);
		// getServerZone().setGroupPermission(DEFAULT_GROUP, "fe.commands.time", true);
		//
		// WorldZone world0 = getWorldZone(0);
		// world0.setGroupPermission(DEFAULT_GROUP, "fe.commands.gamemode", true);
		// world0.setGroupPermission(DEFAULT_GROUP, "fe.commands.time", false);
	}

	public void setPersistenceProvider(IZonePersistenceProvider persistenceProvider)
	{
		this.persistenceProvider = persistenceProvider;
	}

	public void save()
	{
		if (persistenceProvider != null)
			persistenceProvider.save(rootZone.getServerZone());
	}

	public boolean load()
	{
		if (persistenceProvider != null)
		{
			ServerZone serverZone = persistenceProvider.load();
			if (serverZone != null)
			{
				// Set new server zone
				rootZone.setServerZone(serverZone);

				// Rebuild zones map
				zones.clear();
				addZone(rootZone);
				addZone(serverZone);
				for (WorldZone worldZone : serverZone.getWorldZones().values())
				{
					addZone(worldZone);
					for (AreaZone areaZone : worldZone.getAreaZones())
					{
						addZone(areaZone);
					}
				}
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------
	// -- Utilities
	// ------------------------------------------------------------

	public Set<String> enumRegisteredPermissions()
	{
		Set<String> perms = new TreeSet<String>();
		for (Map<String, String> groupPerms : rootZone.getGroupPermissions())
		{
			for (String perm : groupPerms.keySet())
			{
				perms.add(perm);
			}
		}
		return perms;
	}

	public Set<String> enumAllPermissions()
	{
		Set<String> perms = new TreeSet<String>();
		for (Zone zone : zones.values())
		{
			for (Map<String, String> groupPerms : zone.getGroupPermissions())
			{
				for (String perm : groupPerms.keySet())
				{
					perms.add(perm);
				}
			}
			for (Map<String, String> playerPerms : zone.getPlayerPermissions())
			{
				for (String perm : playerPerms.keySet())
				{
					perms.add(perm);
				}
			}
		}
		return perms;
	}

	// ------------------------------------------------------------
	// -- Events
	// ------------------------------------------------------------

	@SubscribeEvent
	public void playerLogin(PlayerLoggedInEvent e)
	{
		for (Zone zone : zones.values())
		{
			zone.updatePlayerIdents();
		}
		getServerZone().setPlayerPermission(new UserIdent(e.player), "fe.commands.heal", true);
	}

	// ------------------------------------------------------------
	// -- Core permission handling
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
	public String getPermission(UserIdent ident, WorldPoint point, WorldArea area, Collection<String> groups, String permissionNode, boolean isProperty)
	{
		// Get world zone
		WorldZone worldZone = getWorldZone(point.getDimension());

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
		zones.add(rootZone.getServerZone());
		zones.add(rootZone);

		return getPermission(zones, ident, groups, permissionNode, isProperty);
	}

	public String getPermission(List<Zone> zones, UserIdent ident, Collection<String> groups, String permissionNode, boolean isProperty)
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
		if (ident != null)
		{
			for (String node : nodes)
			{
				for (Zone zone : zones)
				{
					String result = zone.getPlayerPermission(ident, node);
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
	public void setPlayerPermission(UserIdent ident, String permissionNode, boolean value)
	{
		getServerZone().setPlayerPermission(ident, permissionNode, value);
	}

	@Override
	public void setPlayerPermissionProperty(UserIdent ident, String permissionNode, String value)
	{
		getServerZone().setPlayerPermissionProperty(ident, permissionNode, value);
	}

	@Override
	public void setGroupPermission(String group, String permissionNode, boolean value)
	{
		getServerZone().setGroupPermission(group, permissionNode, value);
	}

	@Override
	public void setGroupPermissionProperty(String group, String permissionNode, String value)
	{
		getServerZone().setGroupPermissionProperty(group, permissionNode, value);
	}

	// ------------------------------------------------------------
	// -- IPermissionProvider
	// ------------------------------------------------------------
	/**
	 * Will return the player if set or the commandSender, if it is an instance of {@link EntityPlayer}
	 */
	public static EntityPlayer contextGetPlayerOrCommandPlayer(IContext context)
	{
		return (context.getPlayer() != null) ? context.getPlayer() : (context.getCommandSender() instanceof EntityPlayer ? (EntityPlayer) context
				.getCommandSender() : null);
	}

	private static boolean contextIsConsole(IContext context)
	{
		return context.getPlayer() == null && context.getCommandSender() != null && !(context.getCommandSender() instanceof EntityPlayer);
	}

	public static boolean contextIsPlayer(IContext context)
	{
		return (context.getPlayer() instanceof EntityPlayer) || (context.getCommandSender() instanceof EntityPlayer);
	}

	public static boolean contextHasPlayer(IContext context)
	{
		return (context.getPlayer() instanceof EntityPlayer) || (context.getCommandSender() instanceof EntityPlayer);
	}

	@Override
	public boolean checkPermission(IContext context, String permissionNode)
	{
		// if (contextIsConsole(context)) return true;

		UserIdent ident = null;
		EntityPlayer player = contextGetPlayerOrCommandPlayer(context);
		WorldPoint loc = null;
		WorldArea area = null;
		int dim = 0;

		if (player != null)
		{
			ident = new UserIdent(player);
			// TODO: should be changed to context.getDimension()
			dim = player.dimension;
		}

		if (context.getTargetLocationStart() != null)
		{
			if (context.getTargetLocationEnd() != null)
			{
				area = new WorldArea(dim, new Point(context.getTargetLocationStart()), new Point(context.getTargetLocationEnd()));
			}
			else
			{
				loc = new WorldPoint(dim, context.getTargetLocationStart());
			}
		}
		else if (context.getSourceLocationStart() != null)
		{
			if (context.getSourceLocationEnd() != null)
			{
				area = new WorldArea(dim, new Point(context.getSourceLocationStart()), new Point(context.getSourceLocationEnd()));
			}
			else
			{
				loc = new WorldPoint(dim, context.getSourceLocationStart());
			}
		}
		else
		{
			if (player != null)
			{
				loc = new WorldPoint(player);
			}
		}

		return checkPermission(getPermission(ident, loc, area, getPlayerGroupNames(player), permissionNode, false));
		// return checkPermission(player, node);
	}

	// ------------------------------------------------------------
	// -- Zones
	// ------------------------------------------------------------

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

	public RootZone getRootZone()
	{
		return rootZone;
	}

	@Override
	public ServerZone getServerZone()
	{
		return getRootZone().getServerZone();
	}

	@Override
	public WorldZone getWorldZone(int dimensionId)
	{
		WorldZone zone = rootZone.getServerZone().getWorldZones().get(dimensionId);
		if (zone == null)
		{
			zone = new WorldZone(getServerZone(), dimensionId);
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
		return zone;
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
	// -- Group
	// ------------------------------------------------------------

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
		return checkPermission(getPermission(new UserIdent(player), new WorldPoint(player), null, getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, String permissionNode)
	{
		return getPermission(new UserIdent(player), new WorldPoint(player), null, getPlayerGroupNames(player), permissionNode, true);
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
		return checkPermission(getPermission(new UserIdent(player), targetPoint, null, getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, WorldPoint targetPoint, String permissionNode)
	{
		return getPermission(new UserIdent(player), targetPoint, null, getPlayerGroupNames(player), permissionNode, true);
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, WorldArea targetArea, String permissionNode)
	{
		return checkPermission(getPermission(new UserIdent(player), null, targetArea, getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, WorldArea targetArea, String permissionNode)
	{
		return getPermission(new UserIdent(player), null, targetArea, getPlayerGroupNames(player), permissionNode, true);
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, Zone zone, String permissionNode)
	{
		List<Zone> zones = new ArrayList<Zone>();
		zones.add(zone);
		zones.add(rootZone.getServerZone());
		zones.add(rootZone);
		return checkPermission(getPermission(zones, new UserIdent(player), getPlayerGroupNames(player), permissionNode, false));
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, Zone zone, String permissionNode)
	{
		List<Zone> zones = new ArrayList<Zone>();
		zones.add(zone);
		zones.add(rootZone.getServerZone());
		zones.add(rootZone);
		return getPermission(zones, new UserIdent(player), getPlayerGroupNames(player), permissionNode, true);
	}

	// ------------------------------------------------------------
}
