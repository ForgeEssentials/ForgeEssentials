package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.Loadable;
import com.google.gson.annotations.Expose;

import net.minecraft.world.entity.player.Player;

/**
 * {@link WorldZone} covers the entirety of a world. Third lowest in priority with next being {@link ServerZone}.
 */
public class WorldZone extends Zone implements Loadable
{

    @Expose(serialize = false)
    protected ServerZone serverZone;

    private String dimensionID;

    private List<AreaZone> areaZones = new ArrayList<>();

    public WorldZone(int id)
    {
        super(id);
    }

    public WorldZone(ServerZone serverZone, String dimensionID, int id)
    {
        this(id);
        this.dimensionID = dimensionID;
        this.serverZone = serverZone;
        this.serverZone.addWorldZone(this);
    }

    public WorldZone(ServerZone serverZone, String dimensionID)
    {
        this(serverZone, dimensionID, serverZone.nextZoneID());
    }

    @Override
    public void afterLoad()
    {
        for (AreaZone zone : areaZones)
            zone.worldZone = this;
    }

    @Override
    public boolean isPlayerInZone(Player player)
    {
        return player.level.dimension().location().toString().equals(dimensionID);
    }

    @Override
    public boolean isInZone(WorldPoint point)
    {
        return point.getDimension().equals(dimensionID);
    }

    @Override
    public boolean isInZone(WorldArea area)
    {
        return area.getDimension().equals(dimensionID);
    }

    @Override
    public boolean isPartOfZone(WorldArea area)
    {
        return area.getDimension().equals(dimensionID);
    }

    @Override
    public String getName()
    {
        return dimensionID;
    }

    @Override
    public Zone getParent()
    {
        return serverZone;
    }

    @Override
    public ServerZone getServerZone()
    {
        return serverZone;
    }

    public String getDimensionID()
    {
        return dimensionID;
    }

    public AreaZone getAreaZone(String areaName)
    {
        for (AreaZone areaZone : areaZones)
        {
            if (areaZone.getShortName().equals(areaName))
            {
                return areaZone;
            }
        }
        return null;
    }

    public boolean removeAreaZone(AreaZone zone)
    {
        if (APIRegistry.getFEEventBus().post(new PermissionEvent.Zone.Delete(getServerZone(), zone)))
            return false;
        return serverZone.removeZone(zone) | areaZones.remove(zone);
    }

    public Collection<AreaZone> getAreaZones()
    {
        return areaZones;
    }

    public void sortAreaZones()
    {
        Collections.sort(areaZones);
    }

    void addAreaZone(AreaZone areaZone)
    {
        areaZones.add(areaZone);
        getServerZone().addZone(areaZone);
        sortAreaZones();
        setDirty();
    }

}