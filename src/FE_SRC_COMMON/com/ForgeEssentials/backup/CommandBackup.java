package com.ForgeEssentials.backup;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

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
		if (args.length != 1)
		{
			new Backup(true);
		}
		else
		{
			if (isInteger(args[0]))
			{
				new Backup(this.parseInt(sender, args[0]), true);
			}
			else
			{
				new Backup(new File(args[0]));
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length != 1)
		{
			new Backup(true);
		}
		else
		{
			if (isInteger(args[0]))
			{
				new Backup(this.parseInt(sender, args[0]), true);
			}
			else
			{
				new Backup(new File(args[0]));
			}
		}
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

	public static boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		// only got here if we didn't return false
		return true;
	}
}
