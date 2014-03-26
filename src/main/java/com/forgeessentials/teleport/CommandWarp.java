package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.teleport.util.TeleportDataManager;
import com.forgeessentials.teleport.util.Warp;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.AreaSelector.WarpPoint;

/**
 * Now uses TeleportCenter.
 * @author Dries007
 */

public class CommandWarp extends FEcmdModuleCommands
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
			String msg = "";
			for (String warp : TeleportDataManager.warps.keySet())
			{
			    msg = warp + ", " + msg;
			}
			ChatUtils.sendMessage(sender, msg);
		}
		else if (args.length == 1)
		{
			if (TeleportDataManager.warps.containsKey(args[0].toLowerCase()))
			{
				if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
				{
					Warp warp = TeleportDataManager.warps.get(args[0].toLowerCase());
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender.username);
					playerInfo.back = new WarpPoint(sender);
					CommandBack.justDied.remove(sender.username);
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
			if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
			{
				if (args[0].equalsIgnoreCase("set"))
				{
					if (TeleportDataManager.warps.containsKey(args[1].toLowerCase()))
					{
						OutputHandler.chatError(sender, Localization.get("command.warp.alreadyexists"));
					}
					else
					{
						TeleportDataManager.addWarp(new Warp(args[1].toLowerCase(), new WarpPoint(sender)));
						OutputHandler.chatConfirmation(sender, Localization.get(Localization.DONE));
					}
				}
				else if (args[0].equalsIgnoreCase("del"))
				{
					if (TeleportDataManager.warps.containsKey(args[1].toLowerCase()))
					{
						TeleportDataManager.removeWarp(TeleportDataManager.warps.get(args[1]));
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
			if (TeleportDataManager.warps.containsKey(args[1].toLowerCase()))
			{
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player != null)
				{
					Warp warp = TeleportDataManager.warps.get(args[1].toLowerCase());
					PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
					TeleportCenter.addToTpQue(warp.getPoint(), player);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
			else
			{
				OutputHandler.felog.info("CommandBlock Error: " + Localization.get("command.warp.notfound"));
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
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".admin", RegGroup.OWNERS);
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, TeleportDataManager.warps.keySet());
		else if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, "set", "del");
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
