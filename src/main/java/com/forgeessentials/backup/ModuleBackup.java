package com.forgeessentials.backup;

import java.io.File;
import java.io.PrintWriter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class, configClass = BackupConfig.class)
public class ModuleBackup {
    @FEModule.Config
    public static BackupConfig config;

    @FEModule.ModuleDir
    public static File moduleDir;

    public static File baseFolder;

    public static void msg(String msg)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (!BackupConfig.enableMsg)
        {
            return;
        }
        try
        {
            if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            {
                OutputHandler.felog.info(msg);
            }
            else
            {
                ChatUtils.sendMessage(server, "[ForgeEssentials] " + msg);
            }
            ServerConfigurationManager manager = server.getConfigurationManager();
            for (String username : manager.getAllUsernames())
            {
                EntityPlayerMP player = manager.func_152612_a(username);
                if (PermissionsManager.checkPerm(player, "ForgeEssentials.backup.msg"))
                {
                    ChatUtils.sendMessage(player, EnumChatFormatting.AQUA + "[ForgeEssentials] " + msg);
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(new WorldSaver());
    }

    @FEModule.ServerInit
    public void serverStarting(FEModuleServerInitEvent e)
    {
        e.registerServerCommand(new CommandBackup());
        if (BackupConfig.autoInterval != 0)
        {
            new AutoBackup();
        }
        if (BackupConfig.worldSaveInterval != 0)
        {
            new AutoWorldSave();
        }
        makeReadme();

        PermissionsManager.registerPermission("fe.backup.msg", RegisteredPermValue.TRUE);
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

    private void makeReadme()
    {
        try
        {
            if (!baseFolder.exists())
            {
                baseFolder.mkdirs();
            }
            File file = new File(baseFolder, "README.txt");
            if (file.exists())
            {
                return;
            }
            PrintWriter pw = new PrintWriter(file);

            pw.println("############");
            pw.println("## WARNING ##");
            pw.println("############");
            pw.println("");
            pw.println("DON'T CHANGE ANYTHING IN THIS FOLDER.");
            pw.println("IF YOU DO, AUTOREMOVE WILL SCREW UP.");
            pw.println("");
            pw.println("If you have problems with this, report an issue and don't put:");
            pw.println("\"Yes, I read the readme\" in the issue or your message on github,");
            pw.println("YOU WILL BE IGNORED.");
            pw.println("- The FE Team");

            pw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
