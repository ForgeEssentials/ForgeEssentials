package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.world.World;

import com.ForgeEssentials.util.AreaSelector.Selection;
import com.ForgeEssentials.util.AreaSelector.WorldArea;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public interface IZoneManager
{

	Zone getWorldZone(World world);

	void deleteZone(String zoneID);

	boolean doesZoneExist(String zoneID);

	Zone getZone(String zoneID);

	boolean createZone(String zoneID, Selection sel, World world);

	Zone getWhichZoneIn(WorldPoint point);

	Zone getWhichZoneIn(WorldArea area);

	ArrayList<Zone> getZoneList();

	Zone getGLOBAL();

	Zone getSUPER();

}
