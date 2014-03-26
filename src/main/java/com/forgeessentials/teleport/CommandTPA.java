package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.teleport.util.TPAdata;
import com.forgeessentials.teleport.util.TickHandlerTP;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.AreaSelector.WarpPoint;

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
			for (TPAdata data : TickHandlerTP.tpaList)
			{
				if (!data.tphere)
				{
					if (data.receiver == sender)
					{
						ChatUtils.sendMessage(data.sender, Localization.get("command.tpa.accepted"));
						ChatUtils.sendMessage(data.receiver, Localization.get("command.tpa.accepted"));
						PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(data.sender.username);
						playerInfo.back = new WarpPoint(data.sender);
						CommandBack.justDied.remove(data.sender.username);
						TickHandlerTP.tpaListToRemove.add(data);
						TeleportCenter.addToTpQue(new WarpPoint(data.receiver), data.sender);
						return;
					}
				}
			}
			return;
		}

		if (args[0].equalsIgnoreCase("decline"))
		{
			for (TPAdata data : TickHandlerTP.tpaList)
			{
				if (!data.tphere)
				{
					if (data.receiver == sender)
					{
						ChatUtils.sendMessage(data.sender, Localization.get("command.tpa.declined"));
						ChatUtils.sendMessage(data.receiver, Localization.get("command.tpa.declined"));
						TickHandlerTP.tpaListToRemove.add(data);
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
			ChatUtils.sendMessage(sender, args[0] + " not found.");
		}
		else
		{
			TickHandlerTP.tpaListToAdd.add(new TPAdata((EntityPlayerMP) sender, receiver, false));

			ChatUtils.sendMessage(sender, Localization.format("command.tpa.sendRequest", receiver.username));
			ChatUtils.sendMessage(receiver, Localization.format("command.tpa.gotRequest", sender.username));
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
