package net.minecraftforge.fe.event.world;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class WorldPreLoadEvent extends Event
{

    public final int dim;

    public WorldPreLoadEvent(int dim)
    {
        this.dim = dim;
    }

}
