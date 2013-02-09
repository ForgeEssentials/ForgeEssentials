package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.world.World;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class ZoneManager
{
	public static IZoneManager manager;

	public static Zone getWorldZone(World world)
	{
		return manager.getWorldZone(world);
	}

	public static void deleteZone(String zoneID)
	{
		manager.deleteZone(zoneID);
	}

	public static boolean doesZoneExist(String zoneID)
	{
		return manager.doesZoneExist(zoneID);
	}

	public static Zone getZone(String zoneID)
	{
		return manager.getZone(zoneID);
	}

	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		return manager.createZone(zoneID, sel, world);
	}

	public static Zone getWhichZoneIn(Point p, World world)
	{
		return manager.getWhichZoneIn(p, world);
	}

	public static Zone getWhichZoneIn(WorldPoint point)
	{
		World world = FunctionHelper.getDimension(point.dim);
		return getWhichZoneIn(point, world);
	}

	public static Zone getWhichZoneIn(AreaBase area, World world)
	{
		return manager.getWhichZoneIn(area, world);
	}
	
	public static ArrayList<Zone> getZoneList()
	{
		return manager.getZoneList();
	}

	public static Zone getGLOBAL()
	{
		return manager.getGLOBAL();
	}

	public static Zone getSUPER()
	{
		return manager.getSUPER();
	}
}
