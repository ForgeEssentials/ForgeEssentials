package com.forgeessentials.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface NamedWorldHandler
{

    static final String WORLD_NAME_END = "the_end";
    static final String WORLD_NAME_NETHER = "the_nether";
    static final String WORLD_NAME_OVERWORLD = "overworld";

    ServerWorld getWorld(String name);


    List<String> getWorldNames();

    public static class DefaultNamedWorldHandler implements NamedWorldHandler
    {

        @Override
        public ServerWorld getWorld(String name)
        {
            name = name.toLowerCase();
            switch (name)
            {
            case WORLD_NAME_OVERWORLD:
                return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
            case WORLD_NAME_NETHER:
                return ServerLifecycleHooks.getCurrentServer().getLevel(World.NETHER);
            case WORLD_NAME_END:
                return ServerLifecycleHooks.getCurrentServer().getLevel(World.END);
            default:
            {
                try
                {
                    return ServerLifecycleHooks.getCurrentServer().getLevel(null);
                }
                catch (NumberFormatException e)
                {
                    return null;
                }
            }
            }
        }

        @Override
        public List<String> getWorldNames()
        {
            return new ArrayList<>(Arrays.asList(WORLD_NAME_OVERWORLD, WORLD_NAME_NETHER, WORLD_NAME_END));
        }

    }

}