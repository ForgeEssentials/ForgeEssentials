package com.ForgeEssentials.backup;

import java.io.File;
import java.io.PrintWriter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class, configClass = BackupConfig.class)
public class ModuleBackup
{
	@FEModule.Config
	public static BackupConfig	config;

	@FEModule.ModuleDir
	public static File			moduleDir;

	public static File			baseFolder;

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
		TickRegistry.registerTickHandler(new WorldSaver(), Side.SERVER);
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
	}

	@PermRegister
	public void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.backup.msg", RegGroup.GUESTS);
		event.registerPermissionLevel("ForgeEssentials.backup", RegGroup.OWNERS);
	}

	@ForgeSubscribe
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

	@ForgeSubscribe
	public void worldLoad(WorldEvent.Load e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			((WorldServer) e.world).canNotSave = !BackupConfig.worldSaving;
		}
	}

	public static void msg(String msg)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (!BackupConfig.enableMsg)
			return;
		try
		{
			if (FMLCommonHandler.instance().getEffectiveSide().isClient()){
				OutputHandler.felog.info(msg);
			}
			else{
			ChatUtils.sendMessage(server, "[ForgeEssentials] " + msg);
			}
			ServerConfigurationManager manager = server.getConfigurationManager();
			for (String username : manager.getAllUsernames())
			{
				EntityPlayerMP player = manager.getPlayerForUsername(username);
				if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, "ForgeEssentials.backup.msg")))
				{
					ChatUtils.sendMessage(player, FEChatFormatCodes.AQUA + "[ForgeEssentials] " + msg);
				}
			}
		}
		catch (Exception e)
		{
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
				return;
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
