package com.forgeessentials.economy.plots;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
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

    public int getValuation()
    {
        return value;
    }

    @SaveableObject.Reconstructor
    public Plot reconstruct(IReconstructData tag)
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
