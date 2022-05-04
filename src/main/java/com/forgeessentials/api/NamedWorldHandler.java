package com.forgeessentials.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface NamedWorldHandler
{

    static final String WORLD_NAME_END = "end";
    static final String WORLD_NAME_NETHER = "nether";
    static final String WORLD_NAME_OVERWORLD = "surface";

    ServerWorld getWorld(String name);

    String getWorldName(int dimId);

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
                    return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(Integer.parseInt(name));
                }
                catch (NumberFormatException e)
                {
                    return null;
                }
            }
            }
        }

        @Override
        public String getWorldName(int dimId)
        {
            switch (dimId)
            {
            case 0:
                return WORLD_NAME_OVERWORLD;
            case -1:
                return WORLD_NAME_NETHER;
            case 1:
                return WORLD_NAME_END;
            default:
                return Integer.toString(dimId);
            }
        }

        @Override
        public List<String> getWorldNames()
        {
            return new ArrayList<>(Arrays.asList(WORLD_NAME_OVERWORLD, WORLD_NAME_NETHER, WORLD_NAME_END));
        }

    }
    
}