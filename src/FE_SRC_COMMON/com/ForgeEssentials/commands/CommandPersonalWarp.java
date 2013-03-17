package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.CommandDataManager;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.PWarp;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

public class CommandPersonalWarp extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "personalwarp";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "pw" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		HashMap<String, PWarp> map = CommandDataManager.pwMap.get(sender.username);

		if (args.length != 2)
		{
			sender.sendChatToPlayer(Localization.get("command.personalwarp.list"));
			sender.sendChatToPlayer(FunctionHelper.niceJoin(map.keySet().toArray()));
		}
		else
		{
			if (args[0].equalsIgnoreCase("goto"))
			{
				if (map.containsKey(args[1]))
				{
					PWarp warp = map.get(args[1]);
					PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender.username);
					playerInfo.back = new WarpPoint(sender);
					TeleportCenter.addToTpQue(warp.getPoint(), sender);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get("command.personalwarp.notfound"));
				}
			}
			else if (args[0].equalsIgnoreCase("add"))
			{
				if (!map.containsKey(args[1]))
				{
					map.put(args[1], new PWarp(sender.username, args[1], new WarpPoint(sender)));
					OutputHandler.chatConfirmation(sender, Localization.get("command.personalwarp.made"));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get("command.personalwarp.alreadyexists"));
				}
			}
			else if (args[0].equalsIgnoreCase("remove"))
			{
				if (map.containsKey(args[1]))
				{
					CommandDataManager.removePWarp(map.get(args[1]));
					map.remove(args[1]);
					OutputHandler.chatConfirmation(sender, Localization.get("command.personalwarp.remove"));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get("command.personalwarp.notfound"));
				}
			}
		}
		CommandDataManager.pwMap.put(sender.username, map);
		CommandDataManager.savePWarps(sender.username);
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "goto", "add", "remove");
		if (args.length == 2)
		{
			if (CommandDataManager.pwMap.get(sender.getCommandSenderName()) == null)
			{
				CommandDataManager.pwMap.put(sender.getCommandSenderName(), new HashMap<String, PWarp>());
			}
			return getListOfStringsFromIterableMatchingLastWord(args, CommandDataManager.pwMap.get(sender.getCommandSenderName()).keySet());
		}
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
