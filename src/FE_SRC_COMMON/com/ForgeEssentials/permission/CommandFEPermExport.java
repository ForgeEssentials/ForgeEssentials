package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandFEPermExport
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		String output = "export";
		if (args.length > 1)
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + " /feperm export [folderName]");
			return;
		}
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + " /feperm export [folderName]");
				return;
			}
			else
			{
				output = args[0];
			}
		}

		OutputHandler.chatConfirmation(sender, " {PermSQL} Starting permission export...");
		startThread(sender, output);
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		String output = "export";
		if (args.length > 1)
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + " /feperm export [folderName]");
			return;
		}
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + " /feperm export [folderName]");
				return;
			}
			else
			{
				output = args[0];
			}
		}

		ChatUtils.sendMessage(sender, " {PermSQL} Starting permission export...");
		startThread(sender, output);
	}

	private static void startThread(ICommandSender sender, String output)
	{
		ExportThread t = new ExportThread(output, sender);
		t.run();
	}

}
