package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.APIHelper;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.Warp;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

/**
 * Now uses TeleportCenter. TODO get rid of DataStorage
 * 
 * @author Dries007
 * 
 */

public class CommandWarp extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "warp";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization.get("command.warp.list"));
			String msg = "";
			for (String warp : TeleportCenter.warps.keySet())
			{
				msg = warp + ", " + msg;
			}
			sender.sendChatToPlayer(msg);
		}
		else if (args.length == 1)
		{
			if (TeleportCenter.warps.containsKey(args[0].toLowerCase()))
			{
				if (APIHelper.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
				{
					Warp warp = TeleportCenter.warps.get(args[0].toLowerCase());
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender);
					playerInfo.back = new WarpPoint(sender);
					TeleportCenter.addToTpQue(warp.getPoint(), sender);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("command.warp.notfound"));
			}
		}
		else if (args.length == 2)
		{
			if (true)
			{
				if (APIHelper.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "admin")))
				{
					if (args[0].equalsIgnoreCase("set"))
					{
						if (TeleportCenter.warps.containsKey(args[1].toLowerCase()))
						{
							OutputHandler.chatError(sender, Localization.get("command.warp.alreadyexists"));
						}
						else
						{
							TeleportCenter.warps.put(args[1].toLowerCase(), new Warp(args[1].toLowerCase(), new WarpPoint(sender)));

							OutputHandler.chatConfirmation(sender, Localization.get(Localization.DONE));
						}
					}
					else if (args[0].equalsIgnoreCase("del"))
					{
						if (TeleportCenter.warps.containsKey(args[1].toLowerCase()))
						{
							TeleportCenter.warps.remove(args[1].toLowerCase());
							OutputHandler.chatConfirmation(sender, Localization.get(Localization.DONE));
						}
						else
						{
							OutputHandler.chatError(sender, Localization.get("command.warp.notfound"));
						}
					}
					else
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, TeleportCenter.warps.keySet());
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, "set", "del");
		}
		else
		{
			return null;
		}
	}

}
