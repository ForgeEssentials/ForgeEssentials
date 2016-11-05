package net.minecraftforge.fe.event.world;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class WorldPreLoadEvent extends Event
{

    public final int dim;

    public WorldPreLoadEvent(int dim)
    {
        this.dim = dim;
    }

}
