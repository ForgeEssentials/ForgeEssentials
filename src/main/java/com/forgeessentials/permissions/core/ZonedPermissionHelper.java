package com.forgeessentials.permissions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

public class ZonedPermissionHelper implements IPermissionsHelper {

	private GlobalZone globalZone = new GlobalZone();

	private Map<Integer, WorldZone> worldZones = new HashMap<Integer, WorldZone>();

	private Map<String, Group> groups = new HashMap<String, Group>();

	public ZonedPermissionHelper()
	{
		// MinecraftForge.EVENT_BUS.register(this);

		// for (World world : DimensionManager.getWorlds())
		// {
		// worldZones.put(world.provider.dimensionId, new WorldZone(world.provider.dimensionId));
		// }

		// TODO: TESTING
		globalZone.setGroupPermission(DEFAULT_GROUP, "fe.commands.gamemode", false);
		globalZone.setGroupPermission(DEFAULT_GROUP, "fe.commands.time", true);

		WorldZone world0 = new WorldZone(0);
		worldZones.put(world0.getDimensionID(), world0);
		world0.setGroupPermission(DEFAULT_GROUP, "fe.commands.gamemode", true);
		world0.setGroupPermission(DEFAULT_GROUP, "fe.commands.time", false);
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
				if (point != null && zone.isPointInZone(point) || area != null && zone.isAreaInZone(area))
				{
					zones.add(zone);
				}
			}
			zones.add(worldZone);
		}
		zones.add(globalZone);

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
		if (isProperty)
		{
			nodes.add(permissionNode);
		}
		else
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
		globalZone.setGroupPermissionProperty(DEFAULT_GROUP, permissionNode, defaultValue);
	}

	@Override
	public void registerPermission(String permissionNode, PermissionsManager.RegisteredPermValue permLevel)
	{
		if (permLevel == RegisteredPermValue.FALSE)
			globalZone.setGroupPermission(DEFAULT_GROUP, permissionNode, false);
		else if (permLevel == RegisteredPermValue.TRUE)
			globalZone.setGroupPermission(DEFAULT_GROUP, permissionNode, true);
		else if (permLevel == RegisteredPermValue.OP)
		{
			globalZone.setGroupPermission(DEFAULT_GROUP, permissionNode, false);
			globalZone.setGroupPermission(OP_GROUP, permissionNode, true);
		}
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
	// -- IPermissionHelper
	// ------------------------------------------------------------

	@Override
	public GlobalZone getGlobalZone()
	{
		return globalZone;
	}

	@Override
	public WorldZone getWorldZone(World world)
	{
		return worldZones.get(world.provider.dimensionId);
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, String permissionNode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, String permissionNode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getPermissionPropertyInt(EntityPlayer player, String permissionNode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, WorldPoint targetPoint, String permissionNode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, WorldPoint targetPoint, String permissionNode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, AreaBase targetArea, String permissionNode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, AreaBase targetArea, String permissionNode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------

	@Override
	public boolean checkPermission(EntityPlayer player, Zone zone, String permissionNode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPermissionProperty(EntityPlayer player, Zone zone, String permissionNode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------

	@Override
	public List<Zone> getZonesAt(WorldPoint worldPoint)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AreaZone> getAreaZonesAt(WorldPoint worldPoint)
	{
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------

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
		// TODO: Implement
		return groups;
	}

	// ------------------------------------------------------------

	// public boolean checkPermission(EntityPlayer player, String permissionNode)
	// {
	// return !getPermission(player, permissionNode, false).equals(PERMISSION_FALSE);
	// }
	//
	// public String getPermissionProperty(EntityPlayer player, String permissionNode)
	// {
	// return getPermission(player, permissionNode, true);
	// }
	//
	// public String getPermission(String permissionNode, boolean isProperty)
	// {
	// return getPermission(null, null, null, null, permissionNode, isProperty);
	// }
	//
	// /**
	// * Get global permission for point
	// *
	// * @param point
	// * @param permissionNode
	// * @param isProperty
	// * @return
	// */
	// public String getPermission(WorldPoint point, String permissionNode, boolean isProperty)
	// {
	// return getPermission(null, point, null, null, permissionNode, isProperty);
	// }
	//
	// /**
	// * @param area
	// * @param permissionNode
	// * @param isProperty
	// * @return
	// */
	// public String getPermission(WorldArea area, String permissionNode, boolean isProperty)
	// {
	// return getPermission(null, null, area, null, permissionNode, isProperty);
	// }
	//
	// /**
	// * @param player
	// * @param permissionNode
	// * @param isProperty
	// * @return
	// */
	// public String getPermission(EntityPlayer player, String permissionNode, boolean isProperty)
	// {
	// return getPermission(player.getPersistentID().toString(), new WorldPoint(player), null, getPlayerGroups(player), permissionNode, isProperty);
	// }
	//
	// /**
	// * @param player
	// * @param point
	// * @param permissionNode
	// * @param isProperty
	// * @return
	// */
	// public String getPermission(EntityPlayer player, WorldPoint point, String permissionNode, boolean isProperty)
	// {
	// return getPermission(player.getPersistentID().toString(), point, null, getPlayerGroups(player), permissionNode, isProperty);
	// }
	//
	// /**
	// * @param player
	// * @param area
	// * @param permissionNode
	// * @param isProperty
	// * @return
	// */
	// public String getPermission(EntityPlayer player, WorldArea area, String permissionNode, boolean isProperty)
	// {
	// return getPermission(player.getPersistentID().toString(), null, area, getPlayerGroups(player), permissionNode, isProperty);
	// }

}
