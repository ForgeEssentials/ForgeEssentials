package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandGroup
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer("TEST! Group parsing");

		if (args.length == 0) // display syntax & possible options for this level
		{
			//OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
		Group group = PermissionsAPI.getGroupForName(args[0]);
		if (group == null)
		{
			// No such group!
			return;
		}
		if (args.length == 1) // display user-specific settings & there values for this player
		{
			//Give group info + syntax to change.
			return;
		}
		/*
		 * Settings
		 */
		if (args[1].equalsIgnoreCase("prefix"))
		{
			if(args.length == 2 || !args[2].contains("\""))
			{
				/*
				 * User has given use:
				 * 
				 * /p group <group> prefix [zone]
				 * /p group <group> prefix
				 */
				//Get current value + syntax to change.
				return;
			}
			else // args[2] must contian "" => set value.
			{
				if(args.length == 3)
				{
					//Zone specified
				}
				//set value
				return;
			}
		}
		if (args[1].equalsIgnoreCase("suffix"))
		{
			if(args.length == 2 || !args[2].contains("\""))
			{
				/*
				 * User has given use:
				 * 
				 * /p group <group> suffix [zone]
				 * /p group <group> suffix
				 */
				//Get current value + syntax to change.
				return;
			}
			else // args[2] must contian "" => set value.
			{
				if(args.length == 3)
				{
					//Zone specified
				}
				//set value
				return;
			}
		}
		/*
		 * Create / remove part
		 */
		if (args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("make"))
		{
			if(args.length == 3)
			{
				//Zone specified
			}
			//Make zone
			return;
		}
		if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("remove"))
		{
			if(args.length == 3)
			{
				//Zone specified
			}
			//Remove zone
			return;
		}
		/*
		 * Parent part
		 */
		if (args[1].equalsIgnoreCase("parent"))
		{
			if(args.length == 2 || !(args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("clear")))
			{
				if(args.length == 3)
				{
					//args 2 = zone
				}
				//Disp current value.
				return;
			}
			else if(args[2].equalsIgnoreCase("set"))
			{
				if(args.length == 4)
				{
					//arg 3 = zone
				}
				//arg 2 = parent
				return;
			}
			else if(args[2].equalsIgnoreCase("clear"))
			{
				if(args.length == 3)
				{
					//arg 2 = zone
				}
				//Clear parent
				return;
			}
		}
		/*
		 * Priority part
		 */
		if (args[1].equalsIgnoreCase("priority"))
		{
			if(args.length == 2)
			{
				//disp current calue
				return;
			}
			if (args.length == 4)
			{
				//zone = arg 3
			}
			// Set prior (arg 2)
		}
		/*
		 * Permissions part
		 */
		if(args.length == 4)
		{
			//Zone is set, args 3 = zone
		}
		if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow"))
		{
			//arg 2 = perm.
			return;
		}
		if(args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny"))
		{
			//arg 2 = perm.
			return;
		}
		if(args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove"))
		{
			//arg 2 = perm.
			return;
		}
		if(args[1].equalsIgnoreCase("get"))
		{
			//arg 2 = perm.
			return;
		}
		
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Copy paste :p
	}

}
