package com.ForgeEssentials.permission;

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
		sender.sendChatToPlayer("TEST! Promote parsing");

		if (args.length == 0)
		{
			//Not possible
			//OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
		EntityPlayerMP player = FunctionHelper.getPlayerFromPartialName(args[0]);
		if (player == null)
		{
			// No such player!
		}
		if (args.length == 1) // display user-specific settings & there values for this player
		{
			// default ladder
			return;
		}
		if(!args[1].equalsIgnoreCase("from"))
		{
			// Ladder specified.
			// Ladder = arg 1
			if(args.length >= 3)
			{
				if(args.length == 4)
				{
					//Zone set.
					// arg 3 = zone
				}
			}
			return;
		}
		if(args[1].equalsIgnoreCase("from"))
		{
			// Ladder specified.
			// Ladder = arg 1
			if(args.length >= 3)
			{
				if(args.length == 4)
				{
					//Zone set.
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
