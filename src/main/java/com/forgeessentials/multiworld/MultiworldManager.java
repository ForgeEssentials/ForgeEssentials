package com.forgeessentials.multiworld;

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
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fe.DimensionManagerHelper;
import net.minecraftforge.fe.event.world.WorldPreLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.NamedWorldHandler;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.MultiworldException.Type;
import com.forgeessentials.multiworld.gen.WorldTypeMultiworld;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Olee
 * @author gnif
 */
public class MultiworldManager extends ServerEventHandler implements NamedWorldHandler
{

    public static final String PERM_PROP_MULTIWORLD = FEPermissions.FE_INTERNAL + ".multiworld";

    public static final String PROVIDER_NORMAL = "normal";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";

    public static final WorldTypeMultiworld WORLD_TYPE_MULTIWORLD = new WorldTypeMultiworld();

    // ============================================================

    /**
     * Registered multiworlds
     */
    protected Map<String, Multiworld> worlds = new HashMap<>();

    /**
     * Registered multiworlds by dimension
     */
    protected Map<Integer, Multiworld> worldsByDim = new HashMap<>();

    /**
     * Mapping from provider classnames to IDs
     */
    protected Map<String, Integer> worldProviderClasses = new HashMap<>();

    /**
     * Mapping from worldType names to WorldType objects
     */
    protected Map<String, WorldType> worldTypes = new HashMap<>();

    /**
     * List of worlds that have been marked for deletion
     */
    protected ArrayList<WorldServer> worldsToDelete = new ArrayList<>();

    /**
     * List of worlds that have been marked for removal
     */
    protected ArrayList<WorldServer> worldsToRemove = new ArrayList<>();

    /**
     * Event handler for new clients that need to know about our worlds
     */
    protected MultiworldEventHandler eventHandler = new MultiworldEventHandler(this);

    private NamedWorldHandler parentNamedWorldHandler;

    // ============================================================

    public MultiworldManager()
    {
        parentNamedWorldHandler = APIRegistry.namedWorldHandler;
        APIRegistry.namedWorldHandler = this;
    }

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
        Map<String, Multiworld> loadedWorlds = DataManager.getInstance().loadAll(Multiworld.class);
        for (Multiworld world : loadedWorlds.values())
        {
            if (world.generatorOptions == null) {
                world.generatorOptions = "";
            }

            worlds.put(world.getName(), world);
            try
            {
                registerWorld(world);
                if (world.loadOnServerStart) {
                    loadWorld(world);
                }
            }
            catch (MultiworldException e)
            {
                switch (e.type)
                {
                case NO_PROVIDER:
                    LoggingHandler.felog.error(String.format(e.type.error, world.provider));
                    break;
                case NO_WORLDTYPE:
                    LoggingHandler.felog.error(String.format(e.type.error, world.worldType));
                    break;
                default:
                    LoggingHandler.felog.error(e.type.error);
                    break;
                }

            }
        }
    }

    public Collection<Multiworld> getWorlds()
    {
        return worlds.values();
    }

    public ImmutableMap<String, Multiworld> getWorldMap()
    {
        return ImmutableMap.copyOf(worlds);
    }

    public Set<Integer> getDimensions()
    {
        return worldsByDim.keySet();
    }

    public Multiworld getMultiworld(int dimensionId)
    {
        return worldsByDim.get(dimensionId);
    }

    public Multiworld getMultiworld(String name)
    {
        return worlds.get(name);
    }

    @Override
    public WorldServer getWorld(String name)
    {
        WorldServer world = parentNamedWorldHandler.getWorld(name);
        if (world != null)
            return world;

        Multiworld mw = getMultiworld(name);
        if (mw != null)
            return mw.getWorldServer();

        return null;
    }

    @Override
    public String getWorldName(int dimId)
    {
        Multiworld mw = getMultiworld(dimId);
        if (mw != null)
            return mw.getName();
        return parentNamedWorldHandler.getWorldName(dimId);
    }

    @Override
    public List<String> getWorldNames()
    {
        List<String> names = parentNamedWorldHandler.getWorldNames();
        names.addAll(worlds.keySet());
        return names;
    }

    /**
     * Register and load a multiworld. If the world fails to load, it won't be registered
     */
    public void addWorld(Multiworld world) throws MultiworldException
    {
        if (worlds.containsKey(world.getName()))
            throw new MultiworldException(Type.ALREADY_EXISTS);
        registerWorld(world);
        loadWorld(world);
        worlds.put(world.getName(), world);
        world.save();
    }

    /**
     * Get a free dimensionID for a new multiworld - minimum dim-id is 10
     */
    public static int getFreeDimensionId()
    {
        int id = 10;
        while (DimensionManager.isDimensionRegistered(id))
            id++;
        return id;
    }

    protected void registerWorld(Multiworld world) throws MultiworldException
    {
        world.providerId = getWorldProviderId(world.provider);
        world.worldTypeObj = getWorldTypeByName(world.worldType);

        // Register dimension with last used id if possible
        if (DimensionManager.isDimensionRegistered(world.dimensionId))
            world.dimensionId = getFreeDimensionId();

        // Handle permission-dim changes
        checkMultiworldPermissions(world);
        APIRegistry.perms.getServerZone().getWorldZone(world.dimensionId)
                .setGroupPermissionProperty(Zone.GROUP_DEFAULT, PERM_PROP_MULTIWORLD, world.getName());

        // Register the dimension
        DimensionManager.registerDimension(world.dimensionId, world.providerId);
        worldsByDim.put(world.dimensionId, world);

        // Allow the world to unload
        DimensionManagerHelper.keepLoaded.put(world.dimensionId, false);
    }

    /**
     * Loads a multiworld
     */
    protected void loadWorld(Multiworld world) throws MultiworldException
    {
        if (world.worldLoaded)
            return;
        try
        {
            // Initialize world settings
            MinecraftServer mcServer = MinecraftServer.getServer();
            WorldServer overworld = DimensionManager.getWorld(0);
            if (overworld == null)
                throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");
            ISaveHandler savehandler = new MultiworldSaveHandler(overworld.getSaveHandler(), world);
            
            WorldSettings settings = new WorldSettings(world.seed, mcServer.getGameType(), mcServer.canStructuresSpawn(), mcServer.isHardcore(), WorldType.parseWorldType(world.worldType));
            settings.setWorldName(world.generatorOptions);
            WorldInfo info = new WorldInfo(settings, world.name);
            WorldServer worldServer = new WorldServerMultiworld(mcServer, savehandler, info, world.dimensionId, settings, overworld, mcServer.theProfiler, world);
            worldServer.init();
            // Overwrite dimensionId because WorldProviderEnd for example just hardcodes the dimId
            worldServer.provider.setDimension(world.dimensionId);
            worldServer.addWorldAccess(new WorldManager(mcServer, worldServer));
            if (!mcServer.isSinglePlayer())
                worldServer.getWorldInfo().setGameType(mcServer.getGameType());
            mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());

            world.updateWorldSettings();
            world.worldLoaded = true;
            world.error = false;

            // Post WorldEvent.Load
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worldServer));

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

    public int getWorldProviderId(String providerName) throws MultiworldException
    {
        switch (providerName.toLowerCase())
        {
        // We use the hardcoded values as some mods just replace the class
        // (BiomesOPlenty)
        case PROVIDER_NORMAL:
            return 0;
        case PROVIDER_HELL:
            return -1;
        case PROVIDER_END:
            return 1;
        default:
            // Otherwise we try to use the provider classname that was supplied
            Integer providerId = worldProviderClasses.get(providerName);
            if (providerId == null)
                throw new MultiworldException(Type.NO_PROVIDER);
            return providerId;
        }
    }

    /**
     * Checks the WorldZone permissions for multiworlds and moves them to the correct dimension if it changed
     */
    private static void checkMultiworldPermissions(Multiworld world)
    {
        for (WorldZone zone : APIRegistry.perms.getServerZone().getWorldZones().values())
        {
            String wn = zone.getGroupPermission(Zone.GROUP_DEFAULT, PERM_PROP_MULTIWORLD);
            if (wn != null && wn.equals(world.getName()))
            {
                if (zone.getDimensionID() != world.dimensionId)
                {
                    WorldZone newZone = APIRegistry.perms.getServerZone().getWorldZone(world.dimensionId);
                    // Swap the permissions of the multiworld with the one
                    // that's currently taking up it's dimID
                    zone.swapPermissions(newZone);
                }
                return;
            }
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
    public void worldPreLoadEvent(WorldPreLoadEvent event)
    {
        Multiworld mw = getMultiworld(event.dim);
        if (mw != null)
        {
            try
            {
                loadWorld(mw);
                event.setCanceled(true);
            }
            catch (MultiworldException e)
            {
                e.printStackTrace();
                Throwables.propagate(e);
            }
        }
    }

    /**
     * Load global world data
     */
    @SubscribeEvent
    public void worldUnloadEvent(WorldEvent.Unload event)
    {
        Multiworld mw = getMultiworld(event.world.provider.getDimensionId());
        if (mw != null)
            mw.worldLoaded = false;
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
            if (DimensionManager.getWorld(world.provider.getDimensionId()) == null)
            {
                if (DimensionManager.isDimensionRegistered(world.provider.getDimensionId()))
                    DimensionManager.unregisterDimension(world.provider.getDimensionId());
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
            if (DimensionManager.getWorld(world.provider.getDimensionId()) == null)
            {
                try
                {
                    if (DimensionManager.isDimensionRegistered(world.provider.getDimensionId()))
                        DimensionManager.unregisterDimension(world.provider.getDimensionId());

                    File path = world.getChunkSaveLocation(); // new
                                                              // File(world.getSaveHandler().getWorldDirectory(),
                                                              // world.provider.getSaveFolder());
                    FileUtils.deleteDirectory(path);

                    it.remove();
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.warn("Error deleting dimension files");
                }
            }
        }
    }

    // ============================================================
    // WorldProvider management

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
                // skip the default providers as these are aliased as 'normal',
                // 'nether' and 'end'
                if (provider.getKey() >= -1 && provider.getKey() <= 1)
                    continue;

                worldProviderClasses.put(provider.getValue().getName(), provider.getKey());
            }
            worldProviderClasses.put(PROVIDER_NORMAL, 0);
            worldProviderClasses.put(PROVIDER_HELL, 1);
            worldProviderClasses.put(PROVIDER_END, -1);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        LoggingHandler.felog.debug("[Multiworld] Available world providers:");
        for (Entry<String, Integer> provider : worldProviderClasses.entrySet())
        {
            LoggingHandler.felog.debug("# " + provider.getValue() + ":" + provider.getKey());
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
    public WorldType getWorldTypeByName(String worldType) throws MultiworldException
    {
        WorldType type = worldTypes.get(worldType.toUpperCase());
        if (type == null)
            throw new MultiworldException(Type.NO_WORLDTYPE);
        return type;
    }

    /**
     * Builds the map of valid worldTypes
     */
    public void loadWorldTypes()
    {
        for (int i = 0; i < WorldType.worldTypes.length; ++i)
        {
            WorldType type = WorldType.worldTypes[i];
            if (type == null)
                continue;

            String name = type.getWorldTypeName().toUpperCase();

            /*
             * MC does not allow creation of this worldType, so we should not either
             */
            if (name.equals("DEFAULT_1_1"))
                continue;

            worldTypes.put(name, type);
        }

        LoggingHandler.felog.debug("[Multiworld] Available world types:");
        for (String worldType : worldTypes.keySet())
            LoggingHandler.felog.debug("# " + worldType);
    }

    public Map<String, WorldType> getWorldTypes()
    {
        return worldTypes;
    }

}
