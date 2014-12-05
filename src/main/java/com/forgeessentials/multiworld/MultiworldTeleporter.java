package com.forgeessentials.multiworld;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * 
 * @author Olee
 */
public class MultiworldTeleporter extends Teleporter {

    protected final WorldServer world;

    public MultiworldTeleporter(WorldServer world)
    {
        super(world);
        this.world = world;
    }

    @Override
    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        return false;
    }

    @Override
    public void removeStalePortalLocations(long totalWorldTime)
    {
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float rotationYaw)
    {
    }

}
