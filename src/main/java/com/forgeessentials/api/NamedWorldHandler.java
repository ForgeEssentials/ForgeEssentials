package com.forgeessentials.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public interface NamedWorldHandler
{

    static final String WORLD_NAME_END = "minecarft:the_end";
    static final String WORLD_NAME_NETHER = "minecarft:the_nether";
    static final String WORLD_NAME_OVERWORLD = "minecarft:overworld";

    static final String SHORT_WORLD_NAME_END = "the_end";
    static final String SHORT_WORLD_NAME_NETHER = "the_nether";
    static final String SHORT_WORLD_NAME_OVERWORLD = "overworld";

    ServerWorld getWorld(String name);

    String getWorldName(String dimId);

    List<String> getWorldNames();

    List<String> getShortWorldNames();

    public static class DefaultNamedWorldHandler implements NamedWorldHandler
    {

        @Override
        public ServerWorld getWorld(String regName)
        {
            regName = regName.toLowerCase();
            switch (regName)
            {
            case SHORT_WORLD_NAME_OVERWORLD:
            case WORLD_NAME_OVERWORLD:
                return ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD);
            case SHORT_WORLD_NAME_NETHER:
            case WORLD_NAME_NETHER:
                return ServerLifecycleHooks.getCurrentServer().getLevel(World.NETHER);
            case SHORT_WORLD_NAME_END:
            case WORLD_NAME_END:
                return ServerLifecycleHooks.getCurrentServer().getLevel(World.END);
            default:
            {
                try
                {
                    ServerWorld world = ServerUtil.getWorldFromString(regName);

                    if (world == null)
                    {
                        LoggingHandler.felog.debug("argument.dimension.invalid" + world);
                        throw new Exception("argument.dimension.invalid" + world);
                    }
                    else
                    {
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

        @Override
        public List<String> getShortWorldNames()
        {
            return new ArrayList<>(
                    Arrays.asList(SHORT_WORLD_NAME_OVERWORLD, SHORT_WORLD_NAME_NETHER, SHORT_WORLD_NAME_END));

        }

    }

}