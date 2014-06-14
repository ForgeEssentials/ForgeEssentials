package com.forgeessentials.worldborder;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.AreaSelector.Point;
import net.minecraft.entity.player.EntityPlayerMP;

@SaveableObject
public class WorldBorder {
    @UniqueLoadingKey
    @SaveableField
    public String zone;

    @SaveableField
    public Point center;

    @SaveableField
    public int rad;

    @SaveableField
    public byte shapeByte;    // 1 = square, 2 = round.

    @SaveableField
    public boolean enabled;

    /**
     * For new borders
     *
     * @param world
     */
    public WorldBorder(Zone zone, Point center, int rad, byte shape)
    {
        if (zone.isGlobalZone() || zone.isWorldZone())
        {
            this.zone = zone.getZoneName();
            this.center = center;
            this.rad = rad;
            shapeByte = shape;
            enabled = true;
        }
        else
        {
            throw new RuntimeException(zone.getZoneName() + " is not the global zone or a worldzone");
        }
    }

    public WorldBorder(Zone zone)
    {
        if (zone.isGlobalZone() || zone.isWorldZone())
        {
            this.zone = zone.getZoneName();
            center = new Point(0, 0, 0);
            rad = 0;
            shapeByte = 0;
            enabled = false;
        }
        else
        {
            throw new RuntimeException(zone.getZoneName() + " is not the global zone or a worldzone");
        }
    }

    public WorldBorder(String zone, Object center, Object rad, Object shapeByte, Object enabled)
    {
        this.zone = zone;
        this.center = (Point) center;
        this.rad = (Integer) rad;
        this.shapeByte = (Byte) shapeByte;
        this.enabled = (Boolean) enabled;
    }

    @Reconstructor
    private static WorldBorder reconstruct(IReconstructData tag)
    {
        return new WorldBorder(tag.getUniqueKey(), tag.getFieldValue("center"), tag.getFieldValue("rad"), tag.getFieldValue("shapeByte"),
                tag.getFieldValue("enabled"));
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
            if (player.posX < center.x - rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posX - (center.x - rad), player);
            }
            if (player.posX > center.x + rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posX - (center.x + rad), player);
            }
            if (player.posZ < center.z - rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posZ - (center.z - rad), player);
            }
            if (player.posZ > center.z + rad)
            {
                ModuleWorldBorder.executeClosestEffects(this, player.posZ - (center.z + rad), player);
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

    public Long getETA()
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
        DataStorageManager.getReccomendedDriver().saveObject(ModuleWorldBorder.con, this);
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
