package net.minecraftforge.fe.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class EntityPortalEvent extends EntityEvent
{

    public final World world;

    public final int x;

    public final int y;

    public final int z;

    public final int targetDimension;

    public EntityPortalEvent(Entity entity, World world, int x, int y, int z, int targetDimension)
    {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.targetDimension = targetDimension;
        System.out.println(String.format("EntityPortalEvent %d %d %d %d", x, y, z, targetDimension));
    }
}
