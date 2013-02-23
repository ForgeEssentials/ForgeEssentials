package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.Warp;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

/**
 * Now uses TeleportCenter. TODO get rid of DataStorage
 * @author Dries007
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
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
				{
					Warp warp = TeleportCenter.warps.get(args[0].toLowerCase());
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender.username);
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
			if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
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

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
		{
			if (TeleportCenter.warps.containsKey(args[0].toLowerCase()))
			{
				List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
				if (PlayerSelector.hasArguments(args[0]))
				{
					players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
				}
				if (players.size() != 0)
				{
					for (EntityPlayer player : players)
					{
						Warp warp = TeleportCenter.warps.get(args[1].toLowerCase());
						PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
						TeleportCenter.addToTpQue(warp.getPoint(), player);
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
			else
			{
				OutputHandler.info("CommandBlock Error: " + Localization.get("command.warp.notfound"));
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock te)
	{
		return true;
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
