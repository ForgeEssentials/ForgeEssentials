package com.forgeessentials.economy.plots;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.selections.WorldArea;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.UUID;

@SaveableObject
public class Plot extends WorldArea{

    @SaveableObject.UniqueLoadingKey
    @SaveableObject.SaveableField
    private String name;

    @SaveableObject.SaveableField
    private int value;

    @SaveableObject.SaveableField
    private String owner;

    public Plot(World world, Point start, Point end, int value, String name, UUID owner)
    {
        super(world, start, end);
        this.name = name;
        this.value = value;
        this.owner = owner.toString();
    }

    public void changeOwner(UUID newOwner)
    {
        this.owner = newOwner.toString();
    }

    public String getName()
    {
        return name;
    }

    public UUID getOwner()
    {
        return UUID.fromString(owner);
    }

    public int getValuation()
    {
        return value;
    }

    @SaveableObject.Reconstructor
    public static Plot reconstruct(IReconstructData tag)
    {
        Point high = (Point) tag.getFieldValue("high");
        Point low = (Point) tag.getFieldValue("low");
        int dim = (Integer) tag.getFieldValue("dim");

        int value = (Integer) tag.getFieldValue("value");
        UUID owner = UUID.fromString((String)tag.getFieldValue("owner"));
        String name = (String)tag.getFieldValue("name");

        return new Plot(DimensionManager.getWorld(dim), high, low, value, name, owner);
    }

}
