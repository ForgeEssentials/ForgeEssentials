package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandUser
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer("TEST!!!!");

		if (args.length == 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
		}
		EntityPlayerMP player = FunctionHelper.getPlayerFromUsername(args[0]);
		if (player == null)
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
		}
		if (args.length == 1) // display user-specific settings
		{

		}
		else if (args[1].equalsIgnoreCase("supers")) // super perms management
		{
			if (args.length == 2) // display user super perms
			{

			}
			else if (args.length >= 3) // changing super perms
			{
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("allow")) // allowing super
																							// perm
				{

				}
				else if (args[2].equalsIgnoreCase("clear") || args[2].equalsIgnoreCase("remove")) // remove super
																									// perm settings
				{

				}
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("deny")) // deny super perm
				{

				}
				if (args.length == 4) // zone portion
				{

				}
			}
			else
			// improper amount of arguments
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			}
		}
		else if (args[1].equalsIgnoreCase("group")) // group management
		{
			if (args[2].equalsIgnoreCase("add")) // add player to group
			{

			}
			else if (args[2].equalsIgnoreCase("remove")) // remove player from
															// group
			{

			}
			else if (args[2].equalsIgnoreCase("set")) // set player's group
			{

			}
			if (args.length == 5) // zone portion
			{

			}
		}
		else if (args.length >= 3) // player management
		{
			if (args[1].equalsIgnoreCase("prefix") || args[1].equalsIgnoreCase("suffix")) // prefix/suffix
																							// changes
			{

			}
			else if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow")) // allowing player
																							// perm
			{

			}
			else if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove")) // remove perm
																								// settings
			{

			}
			else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny")) // deny player perm
			{

			}
			if (args.length == 4) // zone portion
			{

			}
		}
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{

	}

}
