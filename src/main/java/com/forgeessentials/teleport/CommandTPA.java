package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.teleport.util.TPAdata;
import com.forgeessentials.teleport.util.TickHandlerTP;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.AreaSelector.WarpPoint;

public class CommandTPA extends ForgeEssentialsCommandBase
{

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
			OutputHandler.chatError(sender, "Improper syntax. Please try this instead: ");
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
						ChatUtils.sendMessage(data.sender, "Teleport request accepted.");
						ChatUtils.sendMessage(data.receiver, "Teleport request accepted by other party. Teleporting..");
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
						ChatUtils.sendMessage(data.sender, "Teleport request declined.");
						ChatUtils.sendMessage(data.receiver, "Teleport request declined by other party.");
						TickHandlerTP.tpaListToRemove.add(data);
						return;
					}
				}
			}
			return;
		}

		if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".sendrequest")))
		{
			OutputHandler.chatError(sender, "You have insufficient permission to do that. If you believe you received this message in error, please talk to a server admin.");
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

			ChatUtils.sendMessage(sender, String.format("Teleport request sent to %s", receiver.username));
			ChatUtils.sendMessage(receiver, String.format("Received teleport request from %s. Enter '/tpahere accept' to accept, '/tpahere decline' to decline.", sender.username));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "fe.teleport." + getCommandName();
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
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
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/tpa [player] <player|<x> <y> <z|accept|decline>> Request to teleport yourself or another player.";
	}
}
