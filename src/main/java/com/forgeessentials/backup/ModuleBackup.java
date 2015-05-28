package com.forgeessentials.backup;

import java.io.File;
import java.util.Timer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class)
public class ModuleBackup {
    
    @FEModule.ModuleDir
    public static File moduleDir;

    public static File baseFolder;

    private Timer timer = new Timer();

    @SuppressWarnings("unused")
    private WorldSaver worldSaver;

    public static void msg(String msg)
    {
        if (!BackupConfig.enableMsg || msg == null || msg.isEmpty())
            return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null)
            OutputHandler.sendMessage(server, "[ForgeEssentials] " + msg);
        OutputHandler.broadcast(OutputHandler.notification("[ForgeEssentials] " + msg));
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        // Register configuration
        ForgeEssentials.getConfigManager().registerLoader("Backups", new BackupConfig());
        FECommandManager.registerCommand(new CommandBackup());
        
        MinecraftForge.EVENT_BUS.register(this);
        worldSaver = new WorldSaver();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        if (BackupConfig.autoInterval != 0)
        {
            timer.schedule(new AutoBackup(), BackupConfig.autoInterval*60*1000, BackupConfig.autoInterval*60*1000);
        }
        if (BackupConfig.worldSaveInterval != 0)
        {
            timer.schedule(new AutoWorldSave(), BackupConfig.worldSaveInterval*60*1000, BackupConfig.worldSaveInterval*60*1000);
        }

        APIRegistry.perms.registerPermission("fe.backup.msg", RegisteredPermValue.TRUE);
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            if (BackupConfig.backupOnWorldUnload)
            {
                new Backup((WorldServer) e.world, false).run();
            }
        }
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            ((WorldServer) e.world).levelSaving = !BackupConfig.worldSaving;
        }
    }

}
