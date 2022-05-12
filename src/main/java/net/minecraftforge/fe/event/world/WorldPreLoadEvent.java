package net.minecraftforge.fe.event.world;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class WorldPreLoadEvent extends Event
{

    public final int dim;

    public WorldPreLoadEvent(int dim)
    {
        this.dim = dim;
    }

}
