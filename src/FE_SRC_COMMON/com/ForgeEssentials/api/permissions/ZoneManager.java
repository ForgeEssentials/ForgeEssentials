package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

import com.ForgeEssentials.permission.ModulePermissions;
import com.ForgeEssentials.permission.SqlHelper;
import com.ForgeEssentials.permission.ZoneHelper;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldArea;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class ZoneManager
{

	public static Zone getWorldZone(World world)
	{
		return ZoneHelper.getWorldZone(world);
	}

	public static void deleteZone(String zoneID)
	{
		ZoneHelper.deleteZone(zoneID);
	}

	public static boolean doesZoneExist(String zoneID)
	{
		return ZoneHelper.doesZoneExist(zoneID);
	}

	public static Zone getZone(String zoneID)
	{
		return ZoneHelper.getZone(zoneID);
	}

	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		return ZoneHelper.createZone(zoneID, sel, world);
	}

	public static Set<String> zoneSet()
	{
		return ZoneHelper.zoneSet();
	}

	public static Zone getWhichZoneIn(Point p, World world)
	{
		return ZoneHelper.getWhichZoneIn(p, world);
	}

	public static Zone getWhichZoneIn(WorldPoint point)
	{
		World world = FunctionHelper.getDimension(point.dim);
		return getWhichZoneIn(point, world);
	}

	public static Zone getWhichZoneIn(AreaBase area, World world)
	{
		return ZoneHelper.getWhichZoneIn(area, world);
	}
	
	public static ArrayList<Zone> getZoneList()
	{
		return ZoneHelper.getZoneList();
	}

	public static Zone getGLOBAL()
	{
		return ZoneHelper.getGLOBAL();
	}

	public static Zone getSUPER()
	{
		return ZoneHelper.getSUPER();
	}
}
