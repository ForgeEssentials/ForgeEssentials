package com.forgeessentials.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface NamedWorldHandler
{

    static final String WORLD_NAME_END = "minecarft:the_end";
    static final String WORLD_NAME_NETHER = "minecarft:the_nether";
    static final String WORLD_NAME_OVERWORLD = "minecarft:overworld";

    ServerWorld getWorld(String name);

    String getWorldName(String dimId);

    List<String> getWorldNames();

    public static class DefaultNamedWorldHandler implements NamedWorldHandler
    {

        @Override
        public ServerWorld getWorld(String regName)
        {
            regName = regName.toLowerCase();
            switch (regName)
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
                    ServerWorld world = ServerUtil.getWorldFromString(regName);

                    if (world == null) {
                        LoggingHandler.felog.debug("argument.dimension.invalid"+ world);
                        throw new Exception("argument.dimension.invalid"+ world);
                    } else {
                       return world;
                    }
                }
                catch (Exception e)
                {
                    return null;
                }
            }
            }
        }
        
        @Override
        public String getWorldName(String dim)
        {
            switch (dim)
            {
            case WORLD_NAME_OVERWORLD:
                return "overworld";
            case WORLD_NAME_NETHER:
                return "the_nether";
            case WORLD_NAME_END:
                return "the_end";
            default:
                return dim;
            }
        }

        @Override
        public List<String> getWorldNames()
        {
            return new ArrayList<>(Arrays.asList(WORLD_NAME_OVERWORLD, WORLD_NAME_NETHER, WORLD_NAME_END));
        }

    }

}