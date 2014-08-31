package com.forgeessentials.api.permissions;

import com.forgeessentials.util.selections.Selection;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;
import net.minecraft.world.World;

import java.util.ArrayList;

public interface IZoneManager {

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
