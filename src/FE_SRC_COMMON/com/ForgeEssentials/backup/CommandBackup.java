package com.ForgeEssentials.backup;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
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
		Backup b;
		if (args.length != 1)
		{
			b = new Backup(true);
		}
		else
		{
			if (isInteger(args[0]))
			{
				b = new Backup(parseInt(sender, args[0]), true);
			}
			else
			{
				b = new Backup(new File(args[0]));
			}
		}
		
		if (b != null)
			b.startThread();
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		Backup b = null;
		if (args.length != 1)
		{
			b = new Backup(true);
		}
		else
		{
			if (isInteger(args[0]))
			{
				b = new Backup(parseInt(sender, args[0]), true);
			}
			else
			{
				b = new Backup(new File(args[0]));
			}
		}
		
		if (b != null)
			b.startThread();
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer sender)
	{
		return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm()));
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.backup.command";
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
		return true;
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
