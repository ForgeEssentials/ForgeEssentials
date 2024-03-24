package com.forgeessentials.multiworld.v2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.NamedWorldHandler;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.v2.genWorld.ServerWorldMultiworld;
import com.forgeessentials.multiworld.v2.provider.ProviderHelper;
import com.forgeessentials.multiworld.v2.utils.MultiworldException;
import com.forgeessentials.multiworld.v2.utils.MultiworldException.Type;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class MultiworldManager extends ServerEventHandler implements NamedWorldHandler
{
	public static final Function<MinecraftServer, ChunkProgressListenerFactory> CHUNK_STATUS_LISTENER_FACTORY_FIELD =
			getInstanceField(MinecraftServer.class, "progressListenerFactory");
		public static final Function<MinecraftServer, Executor> BACKGROUND_EXECUTOR_FIELD =
			getInstanceField(MinecraftServer.class, "executor");
		public static final Function<MinecraftServer, LevelStorageAccess> ANVIL_CONVERTER_FOR_ANVIL_FILE_FIELD =
			getInstanceField(MinecraftServer.class, "storageSource");

		// helper for making the above private field getters via reflection
		@SuppressWarnings("unchecked") // also throws ClassCastException if the types are wrong
		static <FIELDHOLDER,FIELDTYPE> Function<FIELDHOLDER,FIELDTYPE> getInstanceField(Class<FIELDHOLDER> fieldHolderClass, String fieldName)
		{
			// forge's ORH is needed to reflect into vanilla minecraft java

			return instance -> {
				try
				{
					return (FIELDTYPE)(fieldHolderClass.getField(fieldName));
				}
				catch (IllegalArgumentException | NoSuchFieldException e)
				{
					throw new RuntimeException(e);
				}
            };
		}
	
    public static final String PERM_PROP_MULTIWORLD = FEPermissions.FE_INTERNAL + ".multiworld";

    // ============================================================

    /**
     * Registered multiworlds
     */
    protected Map<String, Multiworld> worlds = new HashMap<>();

    /**
     * List of worlds that have been marked for deletion
     */
    protected ArrayList<File> worldsFoldersToDelete = new ArrayList<>();

    /**
     * List of worlds that have been marked for removal
     */
    protected ArrayList<ServerLevel> worldsToUnloadAndRemove = new ArrayList<>();

    private NamedWorldHandler parentNamedWorldHandler;

    protected ProviderHelper providerHandler = new ProviderHelper();

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
        Map<String, Multiworld> loadedWorlds = DataManager.getInstance().loadAll(Multiworld.class);
        for (Multiworld world : loadedWorlds.values())
        {
            if (world.getGeneratorOptions() == null) {
                world.setGeneratorOptions("");
            }

            worlds.put(world.getName(), world);
            try
            {
                setupMultiworldData(world);
                loadWorld(world);
            }
            catch (MultiworldException e)
            {
                switch (e.type)
                {
                case NO_BIOME_PROVIDER:
                    LoggingHandler.felog.error(String.format(e.type.error, world.getBiomeProvider()));
                    break;
                case NO_DIMENSION_TYPE:
                    LoggingHandler.felog.error(String.format(e.type.error, world.getDimensionType()));
                    break;
                case NO_DIMENSION_SETTINGS:
                    LoggingHandler.felog.error(String.format(e.type.error, world.getDimensionSetting()));
                    break;
                case NO_CHUNK_GENERATOR:
                    LoggingHandler.felog.error(String.format(e.type.error, world.getChunkGenerator()));
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

    public Set<String> getDimensionsNames()
    {
        return worlds.keySet();
    }

    public Multiworld getMultiworld(String name)
    {
        return worlds.get(name);
    }

    @Override
    public ServerLevel getWorld(String name)
    {
    	ServerLevel world = parentNamedWorldHandler.getWorld(name);
        if (world != null)
            return world;

        Multiworld mw = getMultiworld(name);
        if (mw != null)
            return mw.getWorldServer();

        return null;
    }

    @Override
    public String getWorldName(String dimId)
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

    public ProviderHelper getProviderHandler() {
		return providerHandler;
	}

	/**
     * Register and load a multiworld. If the world fails to load, it won't be registered
     */
    public void addWorld(Multiworld world) throws MultiworldException
    {
        if (worlds.containsKey(world.getName()))
            throw new MultiworldException(Type.WORLD_ALREADY_EXISTS);
        setupMultiworldData(world);
        loadWorld(world);
        worlds.put(world.getName(), world);
        world.save();
    }

    protected void setupMultiworldData(Multiworld world) throws MultiworldException
    {
        // Register dimension with last used id if possible if it has default created id
        if(world.getInternalID()<10) {
            int unusedID = 10;
        	for (Multiworld knownWorld : worlds.values()) {
    			if(knownWorld.getInternalID()>=unusedID) {
    				unusedID=knownWorld.getInternalID()+1;
    			}
    		}
        	world.setInternalID(unusedID);
        }
        // Handle permission-dim changes
        checkMultiworldPermissions(world);
        APIRegistry.perms.getServerZone().getWorldZone(world.getResourceName())
                .setGroupPermissionProperty(Zone.GROUP_DEFAULT, PERM_PROP_MULTIWORLD, world.getName());
    }
	private LevelStem dimensionGenerator(MinecraftServer server, Multiworld world) throws MultiworldException
	{
		long seed = BiomeManager.obfuscateSeed(world.getSeed());
		Registry<Biome> biomeRegistry =server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		DimensionType dimType = providerHandler.getDimensionTypeByName(world.getDimensionType());
		BiomeSource biomeProvider = providerHandler.generateBiomeProviderByName(world.getBiomeProvider(), biomeRegistry, seed);
		NoiseGeneratorSettings dimSettings = providerHandler.getDimensionSettingsByName(world.getDimensionSetting());
		ChunkGenerator  chunkGenerator = providerHandler.generateChunkGeneratorByName(
	    		  biomeRegistry, world.getChunkGenerator(), 
	    		  biomeProvider, seed, () -> dimSettings);
		
		if(dimType==null)
			throw new MultiworldException(Type.NULL_DIMENSION_TYPE);
		if(biomeProvider==null)
			throw new MultiworldException(Type.NULL_BIOME_PROVIDER);
		if(dimSettings==null)
			throw new MultiworldException(Type.NULL_DIMENSION_SETTINGS);
		if(chunkGenerator==null)
			throw new MultiworldException(Type.NO_CHUNK_GENERATOR);

		return new LevelStem(() -> dimType, chunkGenerator);
	}

	private ServerLevel createAndRegisterWorldAndDimension(MinecraftServer server, ResourceKey<Level> worldKey, Multiworld world) throws MultiworldException
	{
		@SuppressWarnings("deprecation")
		Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();

		ServerLevel existingLevel = map.get(worldKey);

		if (existingLevel != null) {
			return existingLevel;
		}
		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
		LevelStem dimension = dimensionGenerator(server, world);

		ChunkProgressListenerFactory chunkListenerFactory = CHUNK_STATUS_LISTENER_FACTORY_FIELD.apply(server);
		Executor executor = BACKGROUND_EXECUTOR_FIELD.apply(server);
		LevelStorageAccess levelSave = ANVIL_CONVERTER_FOR_ANVIL_FILE_FIELD.apply(server);

		WorldData serverConfig = server.getWorldData();
		WorldGenSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
		dimensionGeneratorSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());
		DerivedLevelData derivedworldinfo = new DerivedLevelData(serverConfig, serverConfig.overworldData());
		ServerLevel newWorld = new ServerWorldMultiworld(
			server,
			executor,
			levelSave,
			derivedworldinfo,
			worldKey,
			dimension.type(),
			chunkListenerFactory.create(11),
			dimension.generator(),
			dimensionGeneratorSettings.isDebug(),
			world.getSeed(),
			ImmutableList.of(), // "special spawn list"
				// phantoms, raiders, travelling traders, cats are overworld special spawns
				// the dimension loader is hardcoded to initialize preexisting non-overworld worlds with no special spawn lists
				// so this can probably be left empty for best results and spawns should be handled via other means
			false); // "tick time", true for overworld, always false for everything else
		overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder()));
		map.put(worldKey, newWorld);

		// update forge's world cache (very important, if we don't do this then the new world won't tick!)
		server.markWorldsDirty();

		// Post WorldEvent.Load
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld)); // event isn't cancellable

		return newWorld;
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
        	MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        	ResourceKey<Level> worldKey = world.getResourceLocationUnique();

//            ISaveHandler savehandler = new MiltiworldDimensionSavedDataManager(overworld.getSaveHandler(), world);
//
//            WorldSettings settings = new WorldSettings(world.seed, mcServer.getGameType(), mcServer.canStructuresSpawn(), mcServer.isHardcore(), WorldType.parseWorldType(world.worldType));
//            settings.setGeneratorOptions(world.generatorOptions);
//            WorldInfo info = new WorldInfo(settings, world.name);
//            ServerWorld worldServer = new WorldServerMultiworld(mcServer, savehandler, info, world.dimensionId, overworld, mcServer.profiler, world);
//            worldServer.init();
//            // Overwrite dimensionId because WorldProviderEnd for example just hardcodes the dimId
//            worldServer.provider.setDimension(world.dimensionId);
//            worldServer.provider.getDimensionType().setLoadSpawn(false);
//            //mcServer.overworld().dimensionType().
//            //FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).provider.getDimensionType().setLoadSpawn(true);
//            worldServer.addEventListener(new ServerWorldEventHandler(mcServer, worldServer));
//
//            mcServer.setDifficulty(mcServer.getWorldData().getDifficulty(), false);
//            //if (!mcServer.isSingleplayer())
//            //    worldServer.getLevelData().h.setGameType(mcServer.getGameType());

        	ServerLevel worldServer = createAndRegisterWorldAndDimension(server, worldKey, world);
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
                if (zone.getDimensionID() != world.getResourceName())
                {
                    WorldZone newZone = APIRegistry.perms.getServerZone().getWorldZone(world.getResourceName());
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
    public File unregisterWorld(Multiworld world)
    {
    	ServerChunkCache serverW = world.getWorldServer().getChunkSource();
        File folder = ObfuscationReflectionHelper.getPrivateValue(ChunkMap.class, serverW.chunkMap, "storageFolder");
        world.worldLoaded = false;
        world.removeAllPlayersFromWorld();
        worldsToUnloadAndRemove.add(ServerLifecycleHooks.getCurrentServer().getLevel(world.getResourceLocationUnique()));
        worlds.remove(world.getName());
        return folder;
    }

    /**
     * Unload world and delete it's data once onloaded
     * 
     * @param world
     */
    public void deleteWorld(Multiworld world)
    {
        File deleating = unregisterWorld(world);
        worldsFoldersToDelete.add(deleating);
        world.delete();
    }

    /**
     * Remove dimensions and clear multiworld-data when server stopped
     * 
     * (for integrated server)
     */
    public void serverStopping()
    {
        saveAll();
        for (Multiworld world : worlds.values())
        {
            world.worldLoaded = false;
        }
        worlds.clear();
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
        deleteDimensionFolder();
    }

    /**
     * Load global world data
     */
//    @SubscribeEvent
//    public void worldPreLoadEvent(WorldPreLoadEvent event)
//    {
//        Multiworld mw = getMultiworld(event.dim.location().toString());
//        if (mw != null)
//        {
//            try
//            {
//                loadWorld(mw);
//                event.setCanceled(true);
//            }
//            catch (MultiworldException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    } //forge does this for us currently

    /**
     * Load global world data
     */
    @SubscribeEvent
    public void worldUnloadEvent(WorldEvent.Unload event)
    {
        Multiworld mw = getMultiworld(((ServerLevel)event.getWorld()).dimension().location().toString());
        if (mw != null)
            mw.worldLoaded = false;
    }

    /**
     * Unregister all worlds that have been marked for removal
     */
	protected void unregisterDimensions() {
		for (Iterator<ServerLevel> it = worldsToUnloadAndRemove.iterator(); it.hasNext();) {
			ServerLevel world = it.next();
			// Check with DimensionManager, whether the world is still loaded
			if (ServerLifecycleHooks.getCurrentServer().getLevel(world.dimension()) != null) {
				try {
					LoggingHandler.felog.info("[MultiWorld] Saving chunks for level '{}'/{}", world,
							world.dimension().location());
					world.noSave = true;
					world.save((ProgressListener) null, true, true);
					try {
						MinecraftForge.EVENT_BUS.post(
								new net.minecraftforge.event.world.WorldEvent.Unload(world));
						world.close();
					} catch (IOException ioexception1) {
						LoggingHandler.felog.error("Exception closing the level",
								(Throwable) ioexception1);
					}
					@SuppressWarnings("deprecation")
					Map<ResourceKey<Level>, ServerLevel> map = ServerLifecycleHooks.getCurrentServer().forgeGetWorldMap();
					map.remove(world.dimension());
					
					WorldData serverConfig = ServerLifecycleHooks.getCurrentServer().getWorldData();
					WorldGenSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
					MappedRegistry<LevelStem> registry = ObfuscationReflectionHelper.getPrivateValue(WorldGenSettings.class, dimensionGeneratorSettings, "dimensions");
					BiMap<ResourceLocation, LevelStem> storage = ObfuscationReflectionHelper.getPrivateValue(MappedRegistry.class, registry, "storage");
					BiMap<ResourceKey<LevelStem>, LevelStem> keyStorage = ObfuscationReflectionHelper.getPrivateValue(MappedRegistry.class, registry, "keyStorage");
					Map<LevelStem, Lifecycle> lifecycles = ObfuscationReflectionHelper.getPrivateValue(MappedRegistry.class, registry, "lifecycles");
					LevelStem dim = storage.get(world.dimension().location());
					storage.remove(world.dimension().location(), dim);
					keyStorage.remove(world.dimension(), dim);
					lifecycles.remove(dim);
					ObfuscationReflectionHelper.setPrivateValue(WorldGenSettings.class, dimensionGeneratorSettings, registry, "dimensions");
				}catch(Exception e) {
					e.printStackTrace();
					LoggingHandler.felog.error("FAILED TO DELETE WORLD: "+world.dimension().location().toString()+" from dimReg, YOU NEED TO MANUALY REMOVE IT FROM level.dat/data/WorldGenSettings/dimensions");
				}
				it.remove();
			}
		}
	}

    /**
     * Delete all worlds that have been marked for deletion
     */
    protected void deleteDimensionFolder()
    {
        for (Iterator<File> it = worldsFoldersToDelete.iterator(); it.hasNext();)
        {
        	File folder = it.next();
        	
        	try {
				FileUtils.deleteDirectory(folder);
			} catch (IOException e) {
				LoggingHandler.felog.error("Exception deleting the level",
						(Throwable) e);
				e.printStackTrace();
			}
            it.remove();
        }
    }
}
