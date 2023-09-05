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
import com.forgeessentials.multiworld.v2.utils.MultiworldException;
import com.forgeessentials.multiworld.v2.utils.ProviderHelper;
import com.forgeessentials.multiworld.v2.utils.MultiworldException.Type;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.IChunkStatusListenerFactory;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat.LevelSave;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class MultiworldManager extends ServerEventHandler implements NamedWorldHandler
{
	public static final Function<MinecraftServer, IChunkStatusListenerFactory> CHUNK_STATUS_LISTENER_FACTORY_FIELD =
			getInstanceField(MinecraftServer.class, "field_213220_d");
		public static final Function<MinecraftServer, Executor> BACKGROUND_EXECUTOR_FIELD =
			getInstanceField(MinecraftServer.class, "field_213217_au");
		public static final Function<MinecraftServer, LevelSave> ANVIL_CONVERTER_FOR_ANVIL_FILE_FIELD =
			getInstanceField(MinecraftServer.class, "field_71310_m");

		// helper for making the above private field getters via reflection
		@SuppressWarnings("unchecked") // also throws ClassCastException if the types are wrong
		static <FIELDHOLDER,FIELDTYPE> Function<FIELDHOLDER,FIELDTYPE> getInstanceField(Class<FIELDHOLDER> fieldHolderClass, String fieldName)
		{
			// forge's ORH is needed to reflect into vanilla minecraft java
			Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);

			return instance -> {
				try
				{
					return (FIELDTYPE)(field.get(instance));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
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
    protected ArrayList<ServerWorld> worldsToUnloadAndRemove = new ArrayList<>();

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
    public ServerWorld getWorld(String name)
    {
    	ServerWorld world = parentNamedWorldHandler.getWorld(name);
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
	private Dimension dimensionGenerator(MinecraftServer server, Multiworld world) throws MultiworldException
	{
		long seed = BiomeManager.obfuscateSeed(world.getSeed());
		Registry<Biome> biomeRegistry =server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		DimensionType dimType = providerHandler.getDimensionTypeByName(world.getDimensionType());
		BiomeProvider biomeProvider = providerHandler.generateBiomeProviderByName(world.getBiomeProvider(), biomeRegistry, seed);
		DimensionSettings dimSettings = providerHandler.getDimensionSettingsByName(world.getDimensionSetting());
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

		return new Dimension(() -> dimType, chunkGenerator);
	}

	private ServerWorld createAndRegisterWorldAndDimension(MinecraftServer server, RegistryKey<World> worldKey, Multiworld world) throws MultiworldException
	{
		@SuppressWarnings("deprecation")
		Map<RegistryKey<World>, ServerWorld> map = server.forgeGetWorldMap();

		ServerWorld existingLevel = map.get(worldKey);

		if (existingLevel != null) {
			return existingLevel;
		}
		ServerWorld overworld = server.getLevel(World.OVERWORLD);
		RegistryKey<Dimension> dimensionKey = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
		Dimension dimension = dimensionGenerator(server, world);

		IChunkStatusListenerFactory chunkListenerFactory = CHUNK_STATUS_LISTENER_FACTORY_FIELD.apply(server);
		Executor executor = BACKGROUND_EXECUTOR_FIELD.apply(server);
		LevelSave levelSave = ANVIL_CONVERTER_FOR_ANVIL_FILE_FIELD.apply(server);

		IServerConfiguration serverConfig = server.getWorldData();
		DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
		dimensionGeneratorSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());
		DerivedWorldInfo derivedworldinfo = new DerivedWorldInfo(serverConfig, serverConfig.overworldData());
		ServerWorld newWorld = new ServerWorldMultiworld(
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
		overworld.getWorldBorder().addListener(new IBorderListener.Impl(newWorld.getWorldBorder()));
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
        	RegistryKey<World> worldKey = world.getReasourceLocationUnique();

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

        	ServerWorld worldServer = createAndRegisterWorldAndDimension(server, worldKey, world);
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
    	ServerChunkProvider serverW = world.getWorldServer().getChunkSource();
        File folder = ObfuscationReflectionHelper.getPrivateValue(ChunkManager.class, serverW.chunkMap, "field_219270_x");
        world.worldLoaded = false;
        world.removeAllPlayersFromWorld();
        worldsToUnloadAndRemove.add(ServerLifecycleHooks.getCurrentServer().getLevel(world.getReasourceLocationUnique()));
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
        Multiworld mw = getMultiworld(((ServerWorld)event.getWorld()).dimension().location().toString());
        if (mw != null)
            mw.worldLoaded = false;
    }

    /**
     * Unregister all worlds that have been marked for removal
     */
	protected void unregisterDimensions() {
		for (Iterator<ServerWorld> it = worldsToUnloadAndRemove.iterator(); it.hasNext();) {
			ServerWorld world = it.next();
			// Check with DimensionManager, whether the world is still loaded
			if (ServerLifecycleHooks.getCurrentServer().getLevel(world.dimension()) != null) {
				try {
					LoggingHandler.felog.info("[MultiWorld] Saving chunks for level '{}'/{}", world,
							world.dimension().location());
					world.noSave = true;
					world.save((IProgressUpdate) null, true, true);
					try {
						MinecraftForge.EVENT_BUS.post(
								new net.minecraftforge.event.world.WorldEvent.Unload(world));
						world.close();
					} catch (IOException ioexception1) {
						LoggingHandler.felog.error("Exception closing the level",
								(Throwable) ioexception1);
					}
					@SuppressWarnings("deprecation")
					Map<RegistryKey<World>, ServerWorld> map = ServerLifecycleHooks.getCurrentServer().forgeGetWorldMap();
					map.remove(world.dimension());
					
					IServerConfiguration serverConfig = ServerLifecycleHooks.getCurrentServer().getWorldData();
					DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
					SimpleRegistry<Dimension> registry = ObfuscationReflectionHelper.getPrivateValue(DimensionGeneratorSettings.class, dimensionGeneratorSettings, "field_236208_h_");
					BiMap<ResourceLocation, Dimension> storage = ObfuscationReflectionHelper.getPrivateValue(SimpleRegistry.class, registry, "field_82596_a");
					BiMap<RegistryKey<Dimension>, Dimension> keyStorage = ObfuscationReflectionHelper.getPrivateValue(SimpleRegistry.class, registry, "field_239649_bb_");
					Map<Dimension, Lifecycle> lifecycles = ObfuscationReflectionHelper.getPrivateValue(SimpleRegistry.class, registry, "field_243535_bj");
					Dimension dim = storage.get(world.dimension().location());
					storage.remove(world.dimension().location(), dim);
					keyStorage.remove(world.dimension(), dim);
					lifecycles.remove(dim);
					ObfuscationReflectionHelper.setPrivateValue(DimensionGeneratorSettings.class, dimensionGeneratorSettings, registry, "field_236208_h_");
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
