package com.forgeessentials.mapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.mapper.command.CommandMapper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "mapper", parentMod = ForgeEssentials.class, canDisable = true)
public class ModuleMapper extends ConfigLoaderBase
{

    @FEModule.Instance
    protected static ModuleMapper instance;

    protected static File dataDirectory;

    @FEModule.ModuleDir
    private static File mapperDirectory;

    private static ExecutorService chunkUpdateExecutor = Executors.newSingleThreadExecutor();

    public static ModuleMapper getInstance()
    {
        return instance;
    }

    /* ------------------------------------------------------------ */

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
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent event)
    {
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        // localhostOnly = config.get(CONFIG_CAT, "localhostOnly", true, "Allow connections from the web").getBoolean();
        // hostname = config.get(CONFIG_CAT, "hostname", "localhost", "Hostname of your server. Used for QR code generation.").getString();
        // port = config.get(CONFIG_CAT, "port", 27020, "Port to connect remotes to").getInt();
        // useSSL = config.get(CONFIG_CAT, "use_ssl", false,
        // "Protect the communication against network sniffing by encrypting traffic with SSL (You don't really need it - believe me)").getBoolean();
        // passkeyLength = config.get(CONFIG_CAT, "passkey_length", 6, "Length of the randomly generated passkeys").getInt();
    }

    /* ------------------------------------------------------------ */

    public Future<?> updateChunk(int chunkX, int chunkZ)
    {
        ChunkUpdateTask task = new ChunkUpdateTask(chunkX, chunkZ);
        return chunkUpdateExecutor.submit(task);
    }

    public Future<BufferedImage> getChunkImage(final WorldServer world, final int chunkX, final int chunkZ)
    {
        return chunkUpdateExecutor.submit(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception
            {
                // TODO check for cached chunk image
                return MapperUtil.renderChunk(world, chunkX, chunkZ);
            }
        });
    }

    /* ------------------------------------------------------------ */

}
