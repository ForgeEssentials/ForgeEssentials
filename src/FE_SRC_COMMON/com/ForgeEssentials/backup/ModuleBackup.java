package com.ForgeEssentials.backup;

import java.io.File;
import java.io.PrintWriter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
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
	@FEModule.Config
	public static BackupConfig	config;

	@FEModule.ModuleDir
	public static File			moduleDir;

	public static File			baseFolder;

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(this);
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
			if (BackupConfig.backupIfUnloaded)
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
			if (BackupConfig.worldSaveing)
			{
				((WorldServer) e.world).canNotSave = !BackupConfig.worldSaveing;
			}
		}
	}

	public static void msg(String msg)
	{
		OutputHandler.info(msg);
		if (!BackupConfig.enableMsg)
			return;
		try
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			ServerConfigurationManager manager = server.getConfigurationManager();
			server.sendChatToPlayer(msg);
			for (String username : manager.getAllUsernames())
			{
				EntityPlayerMP player = manager.getPlayerForUsername(username);
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, "ForgeEssentials.backup.msg")))
				{
					player.sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
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
	
	synchronized static void worldsave(int i) throws MinecraftException
	{
		WorldServer world = DimensionManager.getWorld(i);
		boolean bl = world.canNotSave;
		world.canNotSave = false;
		world.saveAllChunks(true, (IProgressUpdate) null);
		world.canNotSave = bl;
	}
}
