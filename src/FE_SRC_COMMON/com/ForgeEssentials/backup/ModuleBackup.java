package com.ForgeEssentials.backup;

import java.io.File;
import java.io.PrintWriter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerStop;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

@FEModule(name = "Backups", parentMod = ForgeEssentials.class, configClass = BackupConfig.class)
public class ModuleBackup
{
	@Config
	public static BackupConfig	config;

	@ModuleDir
	public static File			moduleDir;

	public static File			baseFolder;
	public static AutoBackup	autoBackup;
	public static AutoWorldSave	autoWorldSave;

	@Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandBackup());
		autoBackup = new AutoBackup();
		autoWorldSave = new AutoWorldSave();
		makeReadme();
	}

	@ServerStop
	public void serverStopping(FEModuleServerStopEvent e)
	{
		autoBackup.interrupt();
		autoWorldSave.interrupt();
	}

	@PermRegister(ident = "ModuleBackups")
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
			if (config.backupIfUnloaded)
			{
				new Backup((WorldServer) e.world, false);
			}
		}
	}

	@ForgeSubscribe
	public void worldUnload(WorldEvent.Load e)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			if (config.worldSaveing)
			{
				((WorldServer) e.world).canNotSave = !config.worldSaveing;
			}
		}
	}

	public static void msg(String msg)
	{
		OutputHandler.info(msg);
		if (!config.enableMsg)
		{
			return;
		}
		try
		{
			ServerConfigurationManager server = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
			for (String username : server.getAllUsernames())
			{
				EntityPlayerMP player = server.getPlayerForUsername(username);
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, "ForgeEssentials.backup.msg")))
				{
					player.sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
				}
			}
		}
		catch (Exception e)
		{}
	}

	private void makeReadme()
	{
		try
		{
			if (!baseFolder.exists())
				baseFolder.mkdirs();
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
