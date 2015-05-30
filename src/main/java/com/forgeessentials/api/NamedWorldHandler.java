package com.forgeessentials.api;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public interface NamedWorldHandler
{

    static final String WORLD_NAME_END = "end";
    static final String WORLD_NAME_NETHER = "nether";
    static final String WORLD_NAME_SURFACE = "surface";

    WorldServer getWorld(String name);

    String getWorldName(int dimId);

    public static class DefaultNamedWorldHandler implements NamedWorldHandler
    {

        @Override
        public WorldServer getWorld(String name)
        {
            switch (name)
            {
            case WORLD_NAME_SURFACE:
                return DimensionManager.getWorld(0);
            case WORLD_NAME_NETHER:
                return DimensionManager.getWorld(-1);
            case WORLD_NAME_END:
                return DimensionManager.getWorld(1);
            default:
            {
                try
                {
                    return DimensionManager.getWorld(Integer.parseInt(name));
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
                return WORLD_NAME_SURFACE;
            case -1:
                return WORLD_NAME_NETHER;
            case 1:
                return WORLD_NAME_END;
            default:
                return Integer.toString(dimId);
            }
        }

    }
}