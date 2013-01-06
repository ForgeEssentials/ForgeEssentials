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
		sender.sendChatToPlayer("TEST! User parsing");

		if (args.length == 0) // display syntax & possible options for this level
		{
			//OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
		EntityPlayerMP player = FunctionHelper.getPlayerFromUsername(args[0]);
		if (player == null)
		{
			OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			return;
		}
		if (args.length == 1) // display user-specific settings & there values for this player
		{
			
			return;
		}
		else if (args[1].equalsIgnoreCase("supers")) // super perms management
		{
			if (args.length == 2) // display user super perms
			{
				return;
			}
			else if (args.length >= 3) // changing super perms
			{
				Zone zone = ZoneManager.GLOBAL;
				if (args.length == 5) // zone is set
				{
					if(ZoneManager.doesZoneExist(args[4]))
					{
						zone = ZoneManager.getZone(args[4]);
					}
					else
					{
						sender.sendChatToPlayer(Localization.format(Localization.ERROR_ZONE_NOZONE, args[4]));
					}
				}
				
				if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("allow"))
				{
					PermissionsAPI.setPlayerPermission(player.username, args[3], true, zone.getZoneID());
					sender.sendChatToPlayer(player.username + " has been allowed " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("clear") || args[2].equalsIgnoreCase("remove")) // remove super
																									// perm settings
				{
					
					return;
				}
				else if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("deny")) // deny super perm
				{
					PermissionsAPI.setPlayerPermission(player.username, args[3], false, zone.getZoneID());
					sender.sendChatToPlayer(player.username + " has been denied " + args[3]);
					return;
				}
				else if (args[2].equalsIgnoreCase("get"))
				{
					//Get current state.
					return;
				}
				
			}
		}
		else if (args[1].equalsIgnoreCase("group")) // group management
		{
			if (args.length == 5) // zone is set
			{
				
			}
			
			if (args[2].equalsIgnoreCase("add")) // add player to group
			{
				
				return;
			}
			else if (args[2].equalsIgnoreCase("remove")) // remove player from
															// group
			{
				
				return;
			}
			else if (args[2].equalsIgnoreCase("set")) // set player's group
			{
				
				return;
			}
		}
		else if (args.length >= 3) // player management
		{
			if (args.length == 4) // zone is set
			{

			}
			
			if (args[1].equalsIgnoreCase("prefix") || args[1].equalsIgnoreCase("suffix")) // prefix/suffix
																							// changes
			{
				return;
			}
			else if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("allow")) // allowing player
																							// perm
			{
				return;
			}
			else if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("remove")) // remove perm
																								// settings
			{
				return;
			}
			else if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("deny")) // deny player perm
			{
				return;
			}
		}
		
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Copy paste :p
	}

}
