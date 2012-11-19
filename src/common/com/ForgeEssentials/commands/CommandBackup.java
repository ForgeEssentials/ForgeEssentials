package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandBackup extends ForgeEssentialsCommandBase
{

	static List<String>		files;
	static String			backupLoc;
	static MinecraftServer	server;

	@Override
	public String getCommandName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{

	}

	@Override
	// TODO fix this
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer("Not implemented");
		/**
		 * Not implemented
		 * sender.sendChatToPlayer("Starting backup...");
		 * files = new ArrayList<String>();
		 * Calendar calendar = Calendar.getInstance();
		 * if (server.isDedicatedServer()) backupLoc = (new File(server.getFolderName())).getAbsolutePath() + File.separator;
		 * else backupLoc = new File("saves" + File.separator + server.getFolderName()).getAbsolutePath() + File.separator;
		 * File backupDir = new File(ForgeEssentials.FEDIR, "/backups");
		 * if (!backupDir.exists()){
		 * backupDir.mkdirs();
		 * }
		 * sender.sendChatToPlayer("Backing up world from " + backupLoc + "to" + backupDir);
		 * makeFileList(new File(backupLoc));
		 * Integer day = calendar.get(calendar.DAY_OF_MONTH);
		 * Integer month = calendar.get(calendar.MONTH);
		 * Integer year = calendar.get(calendar.YEAR);
		 * Integer hr = calendar.get(calendar.HOUR_OF_DAY);
		 * Integer min = calendar.get(calendar.MINUTE);
		 * }
		 */

	}

	@Override
	public String getSyntaxConsole()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoConsole()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
