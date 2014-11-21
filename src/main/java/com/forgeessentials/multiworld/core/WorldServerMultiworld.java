package com.forgeessentials.multiworld.core;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMultiworld extends WorldServerMulti {
    
    @SuppressWarnings("unused")
    private Multiworld world;

    public WorldServerMultiworld(MinecraftServer mcServer, ISaveHandler saveHandler, String worldname, int dimensionId, WorldSettings worldSettings,
            WorldServer worldServer, Profiler profiler, Multiworld world)
    {
        super(mcServer, saveHandler, worldname, dimensionId, worldSettings, worldServer, profiler);
        this.world = world;
    }

    @Override
    protected void saveLevel() throws MinecraftException
    {
        super.saveLevel();
        this.saveHandler.saveWorldInfo(this.worldInfo);
    }
    
}
