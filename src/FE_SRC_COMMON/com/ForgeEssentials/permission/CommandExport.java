package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandExport
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length > 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + " /feperm export");
			return;
		}

		OutputHandler.chatConfirmation(sender, " {PermSQL} Starting permission export...");
		startThread(sender);
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + " /feperm export");
			return;
		}

		sender.sendChatToPlayer(" {PermSQL} Starting permission export...");
		startThread(sender);
	}

	private static void startThread(ICommandSender sender)
	{
		// TODO: make configureable.
		ExportThread t = new ExportThread("export", sender);
		t.run();
	}

}
