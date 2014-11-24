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

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.core.exception.MultiworldAlreadyExistsException;
import com.forgeessentials.multiworld.core.exception.ProviderNotFoundException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MultiworldManager extends ServerEventHandler {

    public static final String PROVIDER_DEFAULT = "default";
    public static final String PROVIDER_FLAT = "flat";
    public static final String PROVIDER_AMPLIFIED = "amp";
    public static final String PROVIDER_LARGE_BIOMES = "large";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";
    public static final String PROVIDER_CUSTOM = "custom";
    public static final String PROVIDER_CUSTOM_HELL = "custom_nether";
    public static final String PROVIDER_CUSTOM_END = "custom_end";
    
    public static final String[] PROVIDERS = new String[] {
        PROVIDER_DEFAULT,
        PROVIDER_FLAT,
        PROVIDER_AMPLIFIED,
        PROVIDER_LARGE_BIOMES,
        PROVIDER_HELL,
        PROVIDER_END,
    };

    // ============================================================

    public static final WorldTypeMultiworld WORLD_TYPE_MULTIWORLD = new WorldTypeMultiworld();

    // ============================================================

    /**
     * Registered multiworlds
     */
    protected Map<String, Multiworld> worlds = new HashMap<String, Multiworld>();

    /**
     * Registered multiworlds by dimension
     */
    protected Map<Integer, Multiworld> worldsByDim = new HashMap<Integer, Multiworld>();

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
            worlds.put(world.getName(), world);
            try
            {
                loadWorld(world);
            }
            catch (ProviderNotFoundException e)
            {
                OutputHandler.felog.severe("Provider with name \"" + world.provider + "\" not found!");
            }
        }
    }

    public Collection<Multiworld> getWorlds()
    {
        return worlds.values();
    }

    public Set<Integer> getDimensions()
    {
        return worldsByDim.keySet();
    }

    public Multiworld getWorld(int dimensionId)
    {
        return worldsByDim.get(dimensionId);
    }

    public Multiworld getWorld(String name)
    {
        return worlds.get(name);
    }

    public void addWorld(Multiworld world) throws ProviderNotFoundException, MultiworldAlreadyExistsException
    {
        if (worlds.containsKey(world.getName()))
            throw new MultiworldAlreadyExistsException();
        loadWorld(world);
        worlds.put(world.getName(), world);
        world.save();
    }

    protected void loadWorld(Multiworld world) throws ProviderNotFoundException
    {
        if (world.worldLoaded)
            return;
        try {
            initializeMultiworldProvider(world);
            
            // Register dimension with last used id if possible
            if (DimensionManager.isDimensionRegistered(world.dimensionId))
            {
                world.dimensionId = DimensionManager.getNextFreeDimId();
            }
            DimensionManager.registerDimension(world.dimensionId, world.providerId);
            worldsByDim.put(world.dimensionId, world);
    
            // Initialize world settings
            MinecraftServer server = MinecraftServer.getServer();
            WorldServer overworld = DimensionManager.getWorld(0);
            if (overworld == null)
                throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");
            ISaveHandler savehandler = new MultiworldSaveHandler(overworld.getSaveHandler(), world);
            WorldSettings worldSettings = new WorldSettings(world.seed, world.gameType, world.mapFeaturesEnabled, false, world.worldType);
    
            // Create WorldServer with settings
            WorldServer worldServer = new WorldServerMultiworld(server, savehandler, //
                    overworld.getWorldInfo().getWorldName(), world.dimensionId, worldSettings, //
                    overworld, server.theProfiler, world);
            worldServer.addWorldAccess(new WorldManager(server, worldServer));
            world.updateWorldSettings();
            world.worldLoaded = true;
            world.error = false;
    
            // Post WorldEvent.Load
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worldServer));
        }
        catch (Exception e)
        {
            world.error = true;
            throw e;
        }
    }

    public void initializeMultiworldProvider(Multiworld world) throws ProviderNotFoundException
    {
        world.worldType = WorldType.DEFAULT;
        switch (world.provider.toLowerCase())
        {
        case PROVIDER_DEFAULT:
            world.providerId = getProviderIDByClass(WorldProviderSurface.class.getName());
            break;
        case PROVIDER_FLAT:
            world.providerId = getProviderIDByClass(WorldProviderSurface.class.getName());
            world.worldType = WorldType.FLAT;
            break;
        case PROVIDER_AMPLIFIED:
            world.providerId = getProviderIDByClass(WorldProviderSurface.class.getName());
            world.worldType = WorldType.AMPLIFIED;
            break;
        case PROVIDER_LARGE_BIOMES:
            world.providerId = getProviderIDByClass(WorldProviderSurface.class.getName());
            world.worldType = WorldType.LARGE_BIOMES;
            break;
        case PROVIDER_HELL:
            world.providerId = getProviderIDByClass(WorldProviderHell.class.getName());
            break;
        case PROVIDER_END:
            world.providerId = getProviderIDByClass(WorldProviderEnd.class.getName());
            break;
        case PROVIDER_CUSTOM:
            world.providerId = getProviderIDByClass(WorldProviderSurface.class.getName());
            world.worldType = WORLD_TYPE_MULTIWORLD;
            throw new ProviderNotFoundException();
        case PROVIDER_CUSTOM_HELL:
            world.providerId = getProviderIDByClass(WorldProviderHell.class.getName());
            world.worldType = WORLD_TYPE_MULTIWORLD;
            throw new ProviderNotFoundException();
        case PROVIDER_CUSTOM_END:
            world.providerId = getProviderIDByClass(WorldProviderEnd.class.getName());
            world.worldType = WORLD_TYPE_MULTIWORLD;
            throw new ProviderNotFoundException();
        default:
            world.providerId = getProviderIDByClass(world.provider);
            break;
        }
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
        worldsByDim.remove(world.getDimensionId());
        worlds.remove(world.getName());
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
        for (Multiworld world : worlds.values())
        {
            world.worldLoaded = false;
            DimensionManager.unregisterDimension(world.getDimensionId());
        }
        worldsByDim.clear();
        worlds.clear();
    }

    // ============================================================

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
