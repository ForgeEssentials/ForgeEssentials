package com.forgeessentials.util.events.world;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class WorldPreLoadEvent extends Event
{

    public final RegistryKey<World> dim;

    public WorldPreLoadEvent(RegistryKey<World> dim)
    {
        this.dim = dim;
    }

}
