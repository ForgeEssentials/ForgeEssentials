package com.forgeessentials.worldborder;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.data.v2.DataManager;

public class WorldBorder {
    
    public String zone;

    public Point center;

    public int rad;

    public byte shapeByte;    // 1 = square, 2 = round.

    public boolean enabled;

    /**
     * For new borders
     *
     * @param world
     */
    public WorldBorder(Zone zone, Point center, int rad, byte shape)
    {
        if (zone instanceof ServerZone || zone instanceof WorldZone)
        {
            this.zone = zone.getName();
            this.center = center;
            this.rad = rad;
            shapeByte = shape;
            enabled = true;
        }
        else
        {
            throw new RuntimeException(zone.getName() + " is not the global zone or a worldzone");
        }
    }

    public WorldBorder(String zone, Point center, int rad, byte shape, boolean enabled)
    {
        this.zone = zone;
        this.center = center;
        this.rad = rad;
        this.shapeByte = shape;
        this.enabled = enabled;
    }

    public WorldBorder(String name)
    {
        zone = name;
        center = new Point(0, 0, 0);
        rad = 0;
        shapeByte = 0;
        enabled = false;
    }

    public void check(EntityPlayerMP player)
    {
        if (!enabled)
        {
            return;
        }

        // 1 = square
        if (shapeByte == 1)
        {
            if (player.posX < center.getX() - rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posX - (center.getX() - rad), player);
            }
            if (player.posX > center.getX() + rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posX - (center.getX() + rad), player);
            }
            if (player.posZ < center.getZ() - rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posZ - (center.getZ() - rad), player);
            }
            if (player.posZ > center.getZ() + rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posZ - (center.getZ() + rad), player);
            }
        }
        // 2 = round
        else if (shapeByte == 2)
        {
            int dist = ModuleWorldBorder.getDistanceRound(center, player);
            if (dist > rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, dist, player);
            }
        }
    }

    public long getETA()
    {
        try
        {
            // 1 = square
            if (shapeByte == 1)
            {
                return (long) Math.pow(rad / 16 * 2, 2);
            }
            else if (shapeByte == 2)
            {
                return (long) (Math.pow(rad / 16, 2) * Math.PI);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0L;
    }

    public void save()
    {
        DataManager.getInstance().save(this, zone);
    }

    public String getShape()
    {
        switch (shapeByte)
        {
        case 1:
            return "square";
        case 2:
            return "round";
        default:
            return "not set";
        }
    }
}
