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
	//fihgu:
	//TEMP made these infinite loop breeder to return a Fake Zone;
	public static Zone fakeZone = new Zone("Fake Zone");
	
	public static IZoneManager	manager;

	public static Zone getWorldZone(World world)
	{
		return fakeZone;
	}

	public static void deleteZone(String zoneID)
	{
		
	}

	public static boolean doesZoneExist(String zoneID)
	{
		return false;
	}

	public static Zone getZone(String zoneID)
	{
		return fakeZone;
	}

	public static boolean createZone(String zoneID, Selection sel, World world)
	{
		return false;
	}

	public static Zone getWhichZoneIn(Point p, World world)
	{
		return fakeZone;
	}

	public static Zone getWhichZoneIn(WorldPoint point)
	{
		return fakeZone;
	}

	public static Zone getWhichZoneIn(AreaBase area, World world)
	{
		return fakeZone;
	}

	public static ArrayList<Zone> getZoneList()
	{
		ArrayList<Zone> list = new ArrayList<Zone>();
		return list;
	}

	public static Zone getGLOBAL()
	{
		return fakeZone;
	}

	public static Zone getSUPER()
	{
		return fakeZone;
	}
}
