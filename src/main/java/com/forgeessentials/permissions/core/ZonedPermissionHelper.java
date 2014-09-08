package com.forgeessentials.permissions.core;

import com.forgeessentials.api.permissions.*;
import com.forgeessentials.util.selections.Point;
import com.google.common.collect.ImmutableMap;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.api.context.*;

import java.util.*;

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
		globalZone.setGroupPermission(Zone.DEFAULT_GROUP, "fe.commands.gamemode", false);
		globalZone.setGroupPermission(Zone.DEFAULT_GROUP, "fe.commands.time", true);

		WorldZone world0 = new WorldZone(0);
		worldZones.put(world0.getDimensionID(), world0);
		world0.setGroupPermission(Zone.DEFAULT_GROUP, "fe.commands.gamemode", true);
		world0.setGroupPermission(Zone.DEFAULT_GROUP, "fe.commands.time", false);
	}

	@Override
	public String getName()
	{
		return "ForgeEssentials";
	}

	@Override
	public boolean checkPerm(EntityPlayer player, String node, ImmutableMap<String, IContext> contextInfo)
	{
		List<String> groups = new ArrayList<String>();
		// TODO: Get groups

		return checkPermission(player, groups, node);
	}

	public boolean checkPermission(EntityPlayer player, Collection groups, String permissionNode)
	{
		return !getPermission(player, groups, permissionNode, true).equals(Zone.PERMISSION_FALSE);
	}

	public String getPermissionProperty(EntityPlayer player, Collection groups, String permissionNode)
	{
		return getPermission(player, groups, permissionNode, false);
	}

	public String getPermission(EntityPlayer player, Collection<String> groups, String permissionNode, boolean splitPermission)
	{
		// Add default group
		groups.add(Zone.DEFAULT_GROUP);

		// Setup basic data
		String uuid = player.getUniqueID().toString();
		Point position = new Point(player);
		WorldZone worldZone = worldZones.get(player.dimension);

		// Get zones in correct order
		List<Zone> zones = new ArrayList<Zone>();
		if (worldZone != null)
		{
			for (Zone zone : worldZone.getAreaZones())
			{
				if (zone.isPlayerInZone(player))
				{
					zones.add(zone);
				}
			}
			zones.add(worldZone);
		}
		zones.add(globalZone);

		// Build node list
		List<String> nodes = new ArrayList<String>();
		if (splitPermission)
		{
			String[] nodeParts = permissionNode.split("\\.");
			for (int i = nodeParts.length; i >= 0; i--)
			{
				String node = "";
				for (int j = 0; j < i; j++)
				{
					node += nodeParts[j] + ".";
				}
				nodes.add(node + Zone.PERMISSION_ASTERIX);
			}
			nodes.add(Zone.PERMISSION_ASTERIX);
		}
		else
		{
			nodes.add(permissionNode);
		}

		// Check player permissions
		for (String node : nodes)
		{
			for (Zone zone : zones)
			{
				String result = zone.getPlayerPermission(uuid, node);
				if (result != null)
				{
					return result;
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

    public static final IContext GLOBAL = new IContext() {};

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
        return new Point(loc);
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
	public void registerPermission(String node, PermissionsManager.RegisteredPermValue allow)
	{
		// PermissionsManager.registerPermission(node, RegGroup.fromForgeLevel(allow));
	}
}
