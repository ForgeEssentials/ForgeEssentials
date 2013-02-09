package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import net.minecraft.world.World;

import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public interface IZoneManager
{

	Zone getWorldZone(World world);

	void deleteZone(String zoneID);

	boolean doesZoneExist(String zoneID);

	Zone getZone(String zoneID);

	boolean createZone(String zoneID, Selection sel, World world);

	Zone getWhichZoneIn(Point p, World world);

	Zone getWhichZoneIn(AreaBase area, World world);

	ArrayList<Zone> getZoneList();

	Zone getGLOBAL();

	Zone getSUPER();

}
