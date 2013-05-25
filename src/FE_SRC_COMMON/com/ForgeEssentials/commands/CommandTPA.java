package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.TPAdata;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

public class CommandTPA extends FEcmdModuleCommands
{
	/*
	 * Config
	 */
	public static int	timeout	= 25;

	@Override
	public void doConfig(Configuration config, String category)
	{
		timeout = config.get(category, "timeout", 25, "Amount of sec a user has to accept a TPA request").getInt();
	}

	@Override
	public String getCommandName()
	{
		return "tpa";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
			return;
		}

		if (args[0].equalsIgnoreCase("accept"))
		{
			for (TPAdata data : TickHandlerCommands.tpaList)
			{
				if (!data.tphere)
				{
					if (data.receiver == sender)
					{
						data.sender.sendChatToPlayer(Localization.get("command.tpa.accepted"));
						data.receiver.sendChatToPlayer(Localization.get("command.tpa.accepted"));
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(data.sender.username);
						playerInfo.back = new WarpPoint(data.sender);
						CommandBack.justDied.remove(data.sender.username);
						TickHandlerCommands.tpaListToRemove.add(data);
						TeleportCenter.addToTpQue(new WarpPoint(data.receiver), data.sender);
						return;
					}
				}
			}
			return;
		}

		if (args[0].equalsIgnoreCase("decline"))
		{
			for (TPAdata data : TickHandlerCommands.tpaList)
			{
				if (!data.tphere)
				{
					if (data.receiver == sender)
					{
						data.sender.sendChatToPlayer(Localization.get("command.tpa.declined"));
						data.receiver.sendChatToPlayer(Localization.get("command.tpa.declined"));
						TickHandlerCommands.tpaListToRemove.add(data);
						return;
					}
				}
			}
			return;
		}
		
		if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".sendrequest")))
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
			return;
		}
		
		EntityPlayerMP receiver = FunctionHelper.getPlayerForName(sender, args[0]);
		if (receiver == null)
		{
			sender.sendChatToPlayer(args[0] + " not found.");
		}
		else
		{
			TickHandlerCommands.tpaListToAdd.add(new TPAdata((EntityPlayerMP) sender, receiver, false));

			sender.sendChatToPlayer(Localization.format("command.tpa.sendRequest", receiver.username));
			receiver.sendChatToPlayer(Localization.format("command.tpa.gotRequest", sender.username));
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
	public List<?> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{
		if (args.length == 1)
		{
			ArrayList<String> list = new ArrayList<String>();
			list.add("accept");
			list.add("decline");
			list.addAll(Arrays.asList(MinecraftServer.getServer().getAllUsernames()));
			return getListOfStringsFromIterableMatchingLastWord(args, list);
		}
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}
	
	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".sendrequest", getReggroup());
	}
}
