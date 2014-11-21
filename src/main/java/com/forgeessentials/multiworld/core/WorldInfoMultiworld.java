package com.forgeessentials.multiworld.core;

import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;

public class WorldInfoMultiworld extends DerivedWorldInfo {

    protected Multiworld multiworld;

    public WorldInfoMultiworld(WorldInfo worldInfo, Multiworld multiworld)
    {
        super(worldInfo);
        this.multiworld = multiworld;
    }

    /**
     * Returns the seed of current world.
     */
    @Override
    public long getSeed()
    {
        return multiworld.getSeed();
    }

    /**
     * Gets the GameType.
     */
    @Override
    public WorldSettings.GameType getGameType()
    {
        return multiworld.getGameType();
    }

    /**
     * Get current world name
     */
    @Override
    public String getWorldName()
    {
        return "Multiworld #" + multiworld.getDimensionId();
    }

    /**
     * Get current world time
     */
    @Override
    public long getWorldTime()
    {
        return super.getWorldTime();
    }

    /**
     * Get current world time
     */
    @Override
    public long getWorldTotalTime()
    {
        return super.getWorldTotalTime();
    }
}
