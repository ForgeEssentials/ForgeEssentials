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
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.core.exception.MultiworldAlreadyExistsException;
import com.forgeessentials.multiworld.core.exception.ProviderNotFoundException;
import com.forgeessentials.multiworld.core.exception.WorldTypeNotFoundException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MultiworldManager extends ServerEventHandler {

    public static final String PROVIDER_NORMAL = "normal";
    public static final String PROVIDER_HELL   = "nether";
    public static final String PROVIDER_END    = "end";
    
    public static final String[] PROVIDERS = new String[] {
        PROVIDER_NORMAL,
        PROVIDER_HELL,
        PROVIDER_END
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
     * Mapping from worldType names to WorldType objects
     */
    protected Map<String, WorldType> worldTypes = new HashMap<String, WorldType>();

    /**
     * List of worlds that have been marked for deletion
     */
    protected ArrayList<WorldServer> worldsToDelete = new ArrayList<WorldServer>();

    /**
     * List of worlds that have been marked for removal
     */
    protected ArrayList<WorldServer> worldsToRemove = new ArrayList<WorldServer>();

    /**
     * Event handler for new clients that need to know about our worlds
     */
    protected MultiworldEventHandler eventHandler = new MultiworldEventHandler(this);

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
            catch (WorldTypeNotFoundException e)
            {
                OutputHandler.felog.severe("WorldType with name \"" + world.worldType + "\" not found!");
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

    public void addWorld(Multiworld world) throws ProviderNotFoundException, WorldTypeNotFoundException, MultiworldAlreadyExistsException
    {
        if (worlds.containsKey(world.getName()))
            throw new MultiworldAlreadyExistsException();
        loadWorld(world);
        worlds.put(world.getName(), world);
        world.save();
    }

    protected void loadWorld(Multiworld world) throws ProviderNotFoundException, WorldTypeNotFoundException
    {
        if (world.worldLoaded)
            return;
        try {
            OutputHandler.felog.info("WorldType: " + world.worldType);
            initializeMultiworldProvider(world);
            world.worldTypeObj = getWorldTypeByName(world.worldType);
            
            // Register dimension with last used id if possible
            if (DimensionManager.isDimensionRegistered(world.dimensionId))
                world.dimensionId = DimensionManager.getNextFreeDimId();

            // Register the dimension
            DimensionManager.registerDimension(world.dimensionId, world.providerId);
            worldsByDim.put(world.dimensionId, world);
    
            // Initialize world settings
            MinecraftServer server = MinecraftServer.getServer();
            WorldServer overworld = DimensionManager.getWorld(0);
            if (overworld == null)
                throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");
            ISaveHandler savehandler = new MultiworldSaveHandler(overworld.getSaveHandler(), world);
            WorldSettings worldSettings = new WorldSettings(world.seed, world.gameType, world.mapFeaturesEnabled, false, world.worldTypeObj);
    
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

            // This is required otherwise the S01PacketJoinGame.worldinfo may not be initialized
            worldServer.getWorldInfo().setGameType(world.gameType);
            server.func_147139_a(server.func_147135_j());

            // Tell everyone about the new dim
            FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            DimensionRegisterMessage msg = new DimensionRegisterMessage(world.dimensionId, world.providerId);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            channel.writeOutbound(msg);
        }
        catch (Exception e)
        {
            world.error = true;
            throw e;
        }
    }

    public void initializeMultiworldProvider(Multiworld world) throws ProviderNotFoundException
    {
        switch (world.provider.toLowerCase())
        {
        // We use the hardcoded values as some mods outright replace the class (BiomesOPlenty)
        case PROVIDER_NORMAL:        
            world.providerId = 0;
            break;
        case PROVIDER_HELL:
            world.providerId = -1;
            break;
        case PROVIDER_END:
            world.providerId = 1;
            break;

        // Otherwise we try to use the provider classname that was supplied
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
            {
                // skip the default providers as these are aliased as 'normal', 'nether' and 'end'
                if (provider.getKey() >= -1 && provider.getKey() <= 1)
                    continue;

                worldProviderClasses.put(provider.getValue().getName(), provider.getKey());
            }
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        OutputHandler.felog.info("[Multiworld] Available world providers:");
        for (Entry<String, Integer> provider : worldProviderClasses.entrySet())
        {
            OutputHandler.felog.info("# " + provider.getValue() + ":" + provider.getKey());
        }
    }

    public Map<String, Integer> getWorldProviders()
    {
        return worldProviderClasses;
    }

    // ============================================================
    // WorldType management

    /**
     * Returns the WorldType for a given worldType string
     */
    public WorldType getWorldTypeByName(String worldType) throws WorldTypeNotFoundException
    {
        WorldType type = worldTypes.get(worldType.toUpperCase());
        if (type == null)
            throw new WorldTypeNotFoundException();
        return type;
    }

    /**
     * Builds the map of valid worldTypes
     */
    public void loadWorldTypes()
    {
        for (int i = 0; i < WorldType.worldTypes.length; ++i)
            if (WorldType.worldTypes[i] != null)
                worldTypes.put(WorldType.worldTypes[i].getWorldTypeName().toUpperCase(), WorldType.worldTypes[i]);

        OutputHandler.felog.info("[Multiworld] Available world types:");
        for (Entry<String, WorldType> worldType: worldTypes.entrySet())
            OutputHandler.felog.info("# " + worldType.getKey());
    }

    public Map<String, WorldType> getWorldTypes()
    {
        return worldTypes;
    }
}