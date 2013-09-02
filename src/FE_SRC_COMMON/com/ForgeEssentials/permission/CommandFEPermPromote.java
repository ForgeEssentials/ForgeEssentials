package com.ForgeEssentials.permission;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandFEPermPromote
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		ChatUtils.sendMessage(sender, "TEST! Promote parsing");

		if (args.length == 0)
			// Not possible
			// OutputHandler.chatError(sender,
			// Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;

		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player == null)
		{
			// No such player!
		}
		if (args.length == 1)
			// default ladder
			return;
		if (!args[1].equalsIgnoreCase("from"))
		{
			// Ladder specified.
			// Ladder = arg 1
			if (args.length >= 3)
			{
				if (args.length == 4)
				{
					// Zone set.
					// arg 3 = zone
				}
			}
			return;
		}
		if (args[1].equalsIgnoreCase("from"))
		{
			// Ladder specified.
			// Ladder = arg 1
			if (args.length >= 3)
			{
				if (args.length == 4)
				{
					// Zone set.
					// arg 3 = zone
				}
			}
			return;
		}

		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Copy paste :p
	}

}
