package com.ForgeEssentials.backup;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

import java.util.List;

public class CommandBackup extends ForgeEssentialsCommandBase
{

	static String		source;
	static String		output;
	static List<String>	fileList;

	@Override
	public String getCommandName()
	{
		return "backup";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		// saving world.
		sender.sendChatToPlayer("Starting worldsave... You may experience server lag while this is being done.");
		MinecraftServer server = MinecraftServer.getServer();
		if (server.getConfigurationManager() != null)
		{
			server.getConfigurationManager().saveAllPlayerData();
		}
		try
		{
			for (WorldServer worldServer : server.worldServers)
			{
				if (worldServer != null)
				{
					WorldServer var5 = worldServer;
					boolean var6 = var5.canNotSave;
					var5.canNotSave = false;
					var5.saveAllChunks(true, (IProgressUpdate) null);
					var5.canNotSave = var6;
				}
			}
		}
		catch (MinecraftException var7)
		{
			sender.sendChatToPlayer("Could not save world.");
		}

		// backing up.
		ModuleBackup.thread = new BackupThread(sender, server);
		ModuleBackup.thread.run();
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// saving world.
		sender.sendChatToPlayer("Starting worldsave... You may experience server lag while this is being done.");
		MinecraftServer server = MinecraftServer.getServer();
		if (server.getConfigurationManager() != null)
		{
			server.getConfigurationManager().saveAllPlayerData();
		}
		try
		{
			for (WorldServer worldServer : server.worldServers)
			{
				if (worldServer != null)
				{
					WorldServer var5 = worldServer;
					boolean var6 = var5.canNotSave;
					var5.canNotSave = false;
					var5.saveAllChunks(true, (IProgressUpdate) null);
					var5.canNotSave = var6;
				}
			}
		}
		catch (MinecraftException var7)
		{
			sender.sendChatToPlayer("Could not save world.");
		}

		// backing up.
		ModuleBackup.thread = new BackupThread(sender, server);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer sender)
	{
		return PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm()));
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.backup";
	}

}