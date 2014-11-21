package com.forgeessentials.multiworld.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import com.google.common.base.Throwables;

public class WorldServerMultiworld extends WorldServerMulti {

    public WorldServerMultiworld(MinecraftServer mcServer, ISaveHandler saveHandler, String worldname, int dimensionId, WorldSettings worldSettings,
            WorldServer worldServer, Profiler profiler, Multiworld multiworld)
    {
        super(mcServer, saveHandler, worldname, dimensionId, worldSettings, worldServer, profiler);
        // Overwrite worldInfo - this one provides access to the custom seeed
        this.worldInfo = new WorldInfoMultiworld(worldServer.getWorldInfo(), multiworld);
        // Create the ChunkProvider again so it gets the new seed
        this.chunkProvider = this.createChunkProvider();
        // Register the WorldChunkManager again so it gets the new seed
        reregisterWorldChunkManager();
    }

    public void reregisterWorldChunkManager()
    {
        try
        {
            Method registerWorldChunkManager;
            try
            {
                registerWorldChunkManager = WorldProvider.class.getDeclaredMethod("func_76572_b");
            }
            catch (NoSuchMethodException e1)
            {
                try
                {
                    registerWorldChunkManager = WorldProvider.class.getDeclaredMethod("registerWorldChunkManager");
                }
                catch (NoSuchMethodException e2)
                {
                    e2.printStackTrace();
                    Throwables.propagate(e2);
                    return;
                }
            }
            registerWorldChunkManager.setAccessible(true);
            registerWorldChunkManager.invoke(provider);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e)
        {
            e.printStackTrace();
            Throwables.propagate(e);
        }
    }

}
