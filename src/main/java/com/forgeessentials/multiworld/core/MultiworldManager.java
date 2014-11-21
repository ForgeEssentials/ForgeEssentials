package com.forgeessentials.multiworld.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MultiworldManager extends ServerEventHandler {

    private Map<Integer, Multiworld> worldByDim = new HashMap<Integer, Multiworld>();

    private Map<String, Multiworld> worldByName = new HashMap<String, Multiworld>();

    /**
     * Mapping from provider classnames to IDs
     */
    protected Map<String, Integer> worldProviderClasses = new HashMap<String, Integer>();

    /**
     * List of worlds that have been marked for deletion
     */
    protected ArrayList<WorldServer> worldsToDelete = new ArrayList<WorldServer>();

    /**
     * List of worlds that have been marked for removal
     */
    protected ArrayList<WorldServer> worldsToRemove = new ArrayList<WorldServer>();

    // ============================================================

    public void saveAll()
    {
        for (Multiworld world : getWorlds())
        {
            world.save();
        }
    }

    public void load()
    {
        DimensionManager.loadDimensionDataMap(null);
        List<Multiworld> loadedWorlds = DataManager.getInstance().loadAll(Multiworld.class);
        for (Multiworld world : loadedWorlds)
        {
            try
            {
                world.providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(world.providerClass);
            }
            catch (ProviderNotFoundException e)
            {
                OutputHandler.felog.severe("Provider with name \"" + world.providerClass + "\" not found!");
                world.providerClass = null;
                world.error = true;
            }
            addWorld(world);
        }
    }

    public Collection<Multiworld> getWorlds()
    {
        return worldByName.values();
    }

    public Set<Integer> getDimensions()
    {
        return worldByDim.keySet();
    }

    public Multiworld getWorld(int dimensionId)
    {
        return worldByDim.get(dimensionId);
    }

    public Multiworld getWorld(String name)
    {
        return worldByName.get(name);
    }

    public void addWorld(Multiworld world)
    {
        if (worldByName.containsKey(world.getName()))
            throw new RuntimeException("World already added");
        worldByDim.put(world.getDimensionId(), world);
        worldByName.put(world.getName(), world);
        world.loadWorld();
        world.save();
    }

    // ============================================================

    /**
     * Unload world
     * 
     * @param world
     */
    public void unloadWorld(Multiworld world)
    {
        world.worldLoaded = false;
        world.removeAllPlayersFromWorld();
        DimensionManager.unloadWorld(world.getDimensionId());
        worldsToRemove.add(DimensionManager.getWorld(world.getDimensionId()));
        worldByDim.remove(world.getDimensionId());
        worldByName.remove(world.getName());
    }

    /**
     * Unload world and delete it's data once onloaded
     * 
     * @param world
     */
    public void deleteWorld(Multiworld world)
    {
        unloadWorld(world);
        world.delete();
        worldsToDelete.add(DimensionManager.getWorld(world.getDimensionId()));
    }

    /**
     * Remove dimensions and clear multiworld-data when server stopped
     * 
     * (for integrated server)
     */
    public void serverStopped()
    {
        saveAll();
        for (Multiworld world : worldByName.values())
        {
            world.worldLoaded = false;
            DimensionManager.unregisterDimension(world.getDimensionId());
        }
        worldByDim.clear();
        worldByName.clear();
    }

    // ============================================================

    public WorldServer generateWorld(String name)
    {
        try
        {
            // Multiworld world = new Multiworld(WorldProviderHeightmap.class.getName());
            Multiworld world = new Multiworld(name, WorldProviderSurface.class.getName());
            addWorld(world);
            return world.getWorld();
        }
        catch (ProviderNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Forge DimensionManager stores used dimension IDs and does not assign them again, unless they are cleared manually.
     */
    public void clearDimensionMap()
    {
        DimensionManager.loadDimensionDataMap(null);
    }

    // ============================================================
    // Unloading and deleting of worlds

    /**
     * When a world is unloaded and marked as to-be-unregistered, remove it now when it is not needed any more
     */
    @SubscribeEvent
    public void serverTickEvent(ServerTickEvent event)
    {
        unregisterDimensions();
        deleteDimensions();
    }

    /**
     * Load global world data
     */
    @SubscribeEvent
    public void worldUnloadEvent(WorldEvent.Unload event)
    {
        unregisterDimensions();
        deleteDimensions();
    }

    /**
     * Unregister all worlds that have been marked for removal
     */
    protected void unregisterDimensions()
    {
        for (Iterator<WorldServer> it = worldsToRemove.iterator(); it.hasNext();)
        {
            WorldServer world = it.next();
            // Check with DimensionManager, whether the world is still loaded
            if (DimensionManager.getWorld(world.provider.dimensionId) == null)
            {
                if (DimensionManager.isDimensionRegistered(world.provider.dimensionId))
                    DimensionManager.unregisterDimension(world.provider.dimensionId);
                it.remove();
            }
        }
    }

    /**
     * Delete all worlds that have been marked for deletion
     */
    protected void deleteDimensions()
    {
        for (Iterator<WorldServer> it = worldsToDelete.iterator(); it.hasNext();)
        {
            WorldServer world = it.next();
            // Check with DimensionManager, whether the world is still loaded
            if (DimensionManager.getWorld(world.provider.dimensionId) == null)
            {
                try
                {
                    if (DimensionManager.isDimensionRegistered(world.provider.dimensionId))
                        DimensionManager.unregisterDimension(world.provider.dimensionId);

                    File path = world.getChunkSaveLocation(); // new File(world.getSaveHandler().getWorldDirectory(), world.provider.getSaveFolder());
                    FileUtils.deleteDirectory(path);

                    it.remove();
                }
                catch (IOException e)
                {
                    OutputHandler.felog.warning("Error deleting dimension files");
                }
            }
        }
    }

    // ============================================================
    // WorldProvider management

    /**
     * Returns the providerId for a given classname
     */
    public int getProviderIDByClass(String providerClass) throws ProviderNotFoundException
    {
        Integer providerId = worldProviderClasses.get(providerClass);
        if (providerId == null)
            throw new ProviderNotFoundException();
        return providerId;
    }

    /**
     * Use reflection to load the registered WorldProviders
     */
    public void loadWorldProviders()
    {
        try
        {
            Field f_providers = DimensionManager.class.getDeclaredField("providers");
            f_providers.setAccessible(true);
            @SuppressWarnings("unchecked")
            Hashtable<Integer, Class<? extends WorldProvider>> loadedProviders = (Hashtable<Integer, Class<? extends WorldProvider>>) f_providers.get(null);
            for (Entry<Integer, Class<? extends WorldProvider>> provider : loadedProviders.entrySet())
                worldProviderClasses.put(provider.getValue().getName(), provider.getKey());
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        OutputHandler.felog.info("[Multiworld] Available world providers:");
        for (Entry<String, Integer> provider : worldProviderClasses.entrySet())
        {
            OutputHandler.felog.info("#" + provider.getValue() + ":" + provider.getKey());
        }
    }

    public Map<String, Integer> getWorldProviders()
    {
        return worldProviderClasses;
    }

}
