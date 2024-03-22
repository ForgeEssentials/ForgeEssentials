package com.forgeessentials.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraft.server.level.ServerLevel;

public interface NamedWorldHandler
{

    static final String WORLD_NAME_END = "minecarft:the_end";
    static final String WORLD_NAME_NETHER = "minecarft:the_nether";
    static final String WORLD_NAME_OVERWORLD = "minecarft:overworld";

    static final String SHORT_WORLD_NAME_END = "the_end";
    static final String SHORT_WORLD_NAME_NETHER = "the_nether";
    static final String SHORT_WORLD_NAME_OVERWORLD = "overworld";

    ServerLevel getWorld(String name);

    String getWorldName(String dimId);

    List<String> getWorldNames();

    public static class DefaultNamedWorldHandler implements NamedWorldHandler
    {

        @Override
        public ServerLevel getWorld(String regName)
        {
            regName = regName.toLowerCase();
            switch (regName)
            {
            case SHORT_WORLD_NAME_OVERWORLD:
            case WORLD_NAME_OVERWORLD:
                return ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
            case SHORT_WORLD_NAME_NETHER:
            case WORLD_NAME_NETHER:
                return ServerLifecycleHooks.getCurrentServer().getLevel(Level.NETHER);
            case SHORT_WORLD_NAME_END:
            case WORLD_NAME_END:
                return ServerLifecycleHooks.getCurrentServer().getLevel(Level.END);
            default:
            {
                try
                {
                    ServerLevel world = ServerUtil.getWorldFromString(regName);

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
    }
}