package com.forgeessentials.mapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.mapper.command.CommandMapper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "mapper", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleMapper extends ConfigLoaderBase
{

    public static final String TAG_MODIFIED = "lastModified";

    public static final int MAX_UPDATE_INTERVAL = 1000 * 5;
    public static final int MAX_REGION_UPDATE_INTERVAL = 1000 * 10;
    public static final long MAX_CACHE_SAVE_INTERVAL = 1000 * 60;

    public final String CACHE_FILE = "cache.dat";

    @FEModule.Instance
    protected static ModuleMapper instance;

    protected File dataDirectory;

    @FEModule.ModuleDir
    private static File mapperDirectory;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private NBTTagCompound cacheStorage = new NBTTagCompound();

    private long lastCacheSave;

    private Set<Chunk> modifiedChunks = Collections.newSetFromMap(new WeakHashMap<Chunk, Boolean>());

    protected Map<Long, Future<BufferedImage>> regionRenderers = new ConcurrentHashMap<>();

    protected Map<Long, Future<BufferedImage>> chunkRenderers = new ConcurrentHashMap<>();

    public static ModuleMapper getInstance()
    {
        return instance;
    }

    /* ------------------------------------------------------------ */

    public ModuleMapper()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent event)
    {
        FECommandManager.registerCommand(new CommandMapper());

        InputStream is = Object.class.getResourceAsStream("/mapper_colorscheme.txt");
        if (is != null)
            MapperUtil.loadColorScheme(is);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void serverStarting(FEModuleServerInitEvent event)
    {
        dataDirectory = new File(mapperDirectory, MinecraftServer.getServer().getFolderName());
        dataDirectory.mkdirs();
        loadCache();
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent event)
    {
        saveCache(true);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // localhostOnly = config.get(CONFIG_CAT, "localhostOnly", true, "Allow connections from the web").getBoolean();
        // hostname = config.get(CONFIG_CAT, "hostname", "localhost",
        // "Hostname of your server. Used for QR code generation.").getString();
        // port = config.get(CONFIG_CAT, "port", 27020, "Port to connect remotes to").getInt();
        // useSSL = config.get(CONFIG_CAT, "use_ssl", false,
        // "Protect the communication against network sniffing by encrypting traffic with SSL (You don't really need it - believe me)").getBoolean();
        // passkeyLength = config.get(CONFIG_CAT, "passkey_length", 6,
        // "Length of the randomly generated passkeys").getInt();
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void chunkUnloadEvent(ChunkEvent.Unload event)
    {
        if (event.world.isRemote)
            return;
        Chunk chunk = event.getChunk();
        if (chunk.needsSaving(false) && !modifiedChunks.contains(chunk))
        {
            setChunkModified(chunk);
            setRegionModified((WorldServer) chunk.getWorld(), MapperUtil.chunkToRegion(chunk.xPosition), MapperUtil.chunkToRegion(chunk.zPosition));
        }
    }

    @SubscribeEvent
    public synchronized void worldTickEvent(WorldTickEvent event)
    {
        if (event.world.isRemote)
            return;
        WorldServer world = (WorldServer) event.world;
        List<Chunk> chunks = new ArrayList<>(world.theChunkProviderServer.loadedChunks);
        for (Chunk chunk : chunks)
            if (chunk != null && chunk.needsSaving(false) && !modifiedChunks.contains(chunk))
            {
                setChunkModified(chunk);
                setRegionModified(world, MapperUtil.chunkToRegion(chunk.xPosition), MapperUtil.chunkToRegion(chunk.zPosition));
            }
    }

    // @SubscribeEvent
    // public void serverTickEvent(ServerTickEvent event)
    // {
    // if (event.phase == Phase.START || ServerUtil.getPlayerList().isEmpty())
    // return;
    // EntityPlayerMP player = ServerUtil.getPlayerList().get(0);
    // int x = (int) Math.floor(player.posX);
    // int z = (int) Math.floor(player.posZ);
    // WorldServer world = (WorldServer) player.worldObj;
    // getRegionImageAsync(world, MapperUtil.worldToRegion(x), MapperUtil.worldToRegion(z));
    // getChunkImageAsync(world, MapperUtil.worldToChunk(x), MapperUtil.worldToChunk(z));
    // }

    /* ------------------------------------------------------------ */

    /* ------------------------------------------------------------ */

    public synchronized void setChunkModified(Chunk chunk)
    {
        modifiedChunks.add(chunk);
        setChunkModified((WorldServer) chunk.getWorld(), chunk.xPosition, chunk.zPosition);
    }

    public synchronized void unsetChunkModified(Chunk chunk)
    {
        modifiedChunks.remove(chunk);
        unsetChunkModified((WorldServer) chunk.getWorld(), chunk.xPosition, chunk.zPosition);
    }

    public synchronized void setChunkModified(WorldServer world, int chunkX, int chunkZ)
    {
        int regionX = MapperUtil.chunkToRegion(chunkX);
        int regionZ = MapperUtil.chunkToRegion(chunkZ);
        chunkX -= regionX * MapperUtil.REGION_CHUNKS;
        chunkZ -= regionZ * MapperUtil.REGION_CHUNKS;
        int[] cache = getRegionCache(world, regionX, regionZ);
        int index = chunkX + chunkZ * MapperUtil.REGION_CHUNKS;
        if (cache[index] == 0)
            cache[index] = getCurrentMillisInt();
        saveCache(false);
    }

    public synchronized void setRegionModified(WorldServer world, int regionX, int regionZ)
    {
        int[] cache = getRegionCache(world, regionX, regionZ);
        if (cache[MapperUtil.REGION_CHUNK_COUNT] == 0)
            cache[MapperUtil.REGION_CHUNK_COUNT] = getCurrentMillisInt();
        saveCache(false);
    }

    public synchronized void unsetChunkModified(WorldServer world, int chunkX, int chunkZ)
    {
        int regionX = MapperUtil.chunkToRegion(chunkX);
        int regionZ = MapperUtil.chunkToRegion(chunkZ);
        chunkX -= regionX * MapperUtil.REGION_CHUNKS;
        chunkZ -= regionZ * MapperUtil.REGION_CHUNKS;
        int[] cache = getRegionCache(world, regionX, regionZ);
        int index = chunkX + chunkZ * MapperUtil.REGION_CHUNKS;
        cache[index] = 0;
        saveCache(false);
    }

    public synchronized void unsetRegionModified(WorldServer world, int regionX, int regionZ)
    {
        int[] cache = getRegionCache(world, regionX, regionZ);
        cache[MapperUtil.REGION_CHUNK_COUNT] = 0;
        saveCache(false);
    }

    public boolean shouldUpdateChunk(WorldServer world, int chunkX, int chunkZ)
    {
        int regionX = MapperUtil.chunkToRegion(chunkX);
        int regionZ = MapperUtil.chunkToRegion(chunkZ);
        chunkX -= regionX * MapperUtil.REGION_CHUNKS;
        chunkZ -= regionZ * MapperUtil.REGION_CHUNKS;
        int[] cache = getRegionCache(world, regionX, regionZ);
        int index = chunkX + chunkZ * MapperUtil.REGION_CHUNKS;
        int flag = cache[index];
        return flag > 0 && flag < getCurrentMillisInt() - MAX_UPDATE_INTERVAL;
    }

    public boolean shouldUpdateRegion(WorldServer world, int regionX, int regionZ)
    {
        int[] cache = getRegionCache(world, regionX, regionZ);
        int flag = cache[MapperUtil.REGION_CHUNK_COUNT];
        return flag > 0 && flag < getCurrentMillisInt() - MAX_REGION_UPDATE_INTERVAL;
    }

    public synchronized int[] getRegionCache(WorldServer world, int regionX, int regionZ)
    {
        String regionId = String.format("%d-%d.%d", world.provider.getDimensionId(), regionX, regionZ);
        NBTBase tag = cacheStorage.getTag(regionId);
        if (!(tag instanceof NBTTagIntArray) || ((NBTTagIntArray) tag).getIntArray().length != MapperUtil.REGION_CHUNK_COUNT + 1)
        {
            tag = new NBTTagIntArray(new int[MapperUtil.REGION_CHUNK_COUNT + 1]);
            cacheStorage.setTag(regionId, tag);
        }
        return ((NBTTagIntArray) tag).getIntArray();
    }

    /* ------------------------------------------------------------ */

    private int getCurrentMillisInt()
    {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public void loadCache()
    {
        try
        {
            FileInputStream is = new FileInputStream(new File(dataDirectory, CACHE_FILE));
            cacheStorage = CompressedStreamTools.readCompressed(is);
        }
        catch (IOException e)
        {
            cacheStorage = new NBTTagCompound();
        }
    }

    public void saveCache(boolean force)
    {
        if (!force && lastCacheSave > System.currentTimeMillis() - MAX_CACHE_SAVE_INTERVAL)
            return;
        try
        {
            FileOutputStream os = new FileOutputStream(new File(dataDirectory, CACHE_FILE));
            CompressedStreamTools.writeCompressed(cacheStorage, os);
            lastCacheSave = System.currentTimeMillis();
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Error saving mapping cache");
        }
    }

    /* ------------------------------------------------------------ */

    public File getChunkCacheFile(final WorldServer world, final int chunkX, final int chunkZ)
    {
        return new File(dataDirectory, String.format("%d.c.%d.%d.png", world.provider.getDimensionId(), chunkX, chunkZ));
    }

    public BufferedImage renderChunk(final WorldServer world, final int chunkX, final int chunkZ)
    {
        if (!MapperUtil.chunkExists(world, chunkX, chunkZ))
            return null;
        File cacheFile = getChunkCacheFile(world, chunkX, chunkZ);
        LoggingHandler.felog.warn(String.format("Rendering chunk %d.%d...", chunkX, chunkZ));
        Chunk chunk = MapperUtil.loadChunk(world, chunkX, chunkZ);
        BufferedImage image = MapperUtil.renderChunk(chunk);
        try
        {
            ImageIO.write(image, "png", cacheFile);
            unsetChunkModified(chunk);
            saveCache(false);
        }
        catch (IOException e)
        {
            LoggingHandler.felog.warn(String.format("Error writing mapper cache file %s: %s", cacheFile, e.getMessage()));
        }
        return image;
    }

    public BufferedImage getChunkImage(final WorldServer world, final int chunkX, final int chunkZ)
    {
        File cacheFile = getChunkCacheFile(world, chunkX, chunkZ);
        if (cacheFile.exists() && !shouldUpdateChunk(world, chunkX, chunkZ))
        {
            try
            {
                return ImageIO.read(cacheFile);
            }
            catch (IOException e)
            {
                LoggingHandler.felog.warn(String.format("Error reading mapper cache file %s"));
            }
        }
        return renderChunk(world, chunkX, chunkZ);
    }

    public synchronized Future<BufferedImage> getChunkImageAsync(final WorldServer world, final int chunkX, final int chunkZ)
    {
        final long id = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        Future<BufferedImage> result = chunkRenderers.get(id);
        if (result != null)
            return result;
        result = executor.submit(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call()
            {
                BufferedImage result = getChunkImage(world, chunkX, chunkZ);
                chunkRenderers.remove(id);
                return result;
            }
        });
        chunkRenderers.put(id, result);
        return result;
    }

    public Future<File> getChunkFileAsync(final WorldServer world, final int chunkX, final int chunkZ)
    {
        final Future<BufferedImage> future = getChunkImageAsync(world, chunkX, chunkZ);
        return executor.submit(new Callable<File>() {
            @Override
            public File call()
            {
                try
                {
                    if (future.get() == null)
                        return null;
                    return getChunkCacheFile(world, chunkX, chunkZ);
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    /* ------------------------------------------------------------ */

    public File getRegionCacheFile(final WorldServer world, final int regionX, final int regionZ)
    {
        return new File(dataDirectory, String.format("%d.%d.%d.png", world.provider.getDimensionId(), regionX, regionZ));
    }

    public BufferedImage renderRegion(WorldServer world, int regionX, int regionZ)
    {
        LoggingHandler.felog.warn(String.format("Rendering region %d.%d...", regionX, regionZ));
        // image = MapperUtil.renderRegion(world, regionX, regionZ);
        BufferedImage image = MapperUtil.renderRegion(world, regionX, regionZ);
        LoggingHandler.felog.warn("Finished!");
        File cacheFile = getRegionCacheFile(world, regionX, regionZ);
        try
        {
            ImageIO.write(image, "png", cacheFile);
            unsetRegionModified(world, regionX, regionZ);
            saveCache(false);
        }
        catch (IOException e)
        {
            LoggingHandler.felog.warn(String.format("Error writing mapper cache file %s: %s", cacheFile, e.getMessage()));
        }
        return image;
    }

    public BufferedImage getRegionImage(WorldServer world, int regionX, int regionZ)
    {
        File cacheFile = getRegionCacheFile(world, regionX, regionZ);
        if (cacheFile.exists() && !shouldUpdateRegion(world, regionX, regionZ))
        {
            try
            {
                return ImageIO.read(cacheFile);
            }
            catch (IOException e)
            {
                LoggingHandler.felog.warn(String.format("Error reading mapper cache file %s", cacheFile));
            }
        }
        return renderRegion(world, regionX, regionZ);
    }

    public synchronized Future<BufferedImage> getRegionImageAsync(final WorldServer world, final int regionX, final int regionZ)
    {
        final long id = ChunkCoordIntPair.chunkXZ2Int(regionX, regionZ);
        Future<BufferedImage> result = regionRenderers.get(id);
        if (result != null)
            return result;
        result = executor.submit(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call()
            {
                BufferedImage result = getRegionImage(world, regionX, regionZ);
                regionRenderers.remove(id);
                return result;
            }
        });
        regionRenderers.put(id, result);
        return result;
    }

    public Future<File> getRegionFileAsync(final WorldServer world, final int regionX, final int regionZ)
    {
        final Future<BufferedImage> future = getRegionImageAsync(world, regionX, regionZ);
        return executor.submit(new Callable<File>() {
            @Override
            public File call()
            {
                try
                {
                    if (future.get() == null)
                        return null;
                    return getRegionCacheFile(world, regionX, regionZ);
                }
                catch (InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    /* ------------------------------------------------------------ */

}
