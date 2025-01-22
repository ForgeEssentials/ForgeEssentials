package net.minecraftforge.fe.event.world;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;


/**
 * Fired when an entity triggers a pressure plate or trip wire.
 *
 * Cancel to prevent activation.
 *
 * FE NOTE: PR directly to net.minecraftforge.event.entity.EntityEvent
 */
@Cancelable
public class PressurePlateEvent extends EntityEvent
{

    public PressurePlateEvent(Entity entity)
    {
        super(entity);
    }

}
