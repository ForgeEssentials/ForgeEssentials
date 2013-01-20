package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.APIHelper;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

public class CommandHome extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "home";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			// homes aren't saving...
			WarpPoint home = PlayerInfo.getPlayerInfo(sender).home;
			if (home == null)
			{
				OutputHandler.chatError(sender, Localization.get("message.error.nohome") + getSyntaxPlayer(sender));
			}
			else
			{
				EntityPlayerMP player = ((EntityPlayerMP) sender);
				PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player);
				playerInfo.back = new WarpPoint(player);
				TeleportCenter.addToTpQue(home, player);
			}
		}
		else if (APIHelper.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".set")))
		{
			if (args.length >= 1 && (args[0].equals("here") || args[0].equals("set")))
			{
				WarpPoint p = new WarpPoint(sender);
				PlayerInfo.getPlayerInfo(sender).home = p;
				sender.sendChatToPlayer(Localization.format("command.home.confirm", p.getX(), p.getY(), p.getZ()));
			}
			else if (args.length >= 3)
			{
				int x = 0;
				int y = 0;
				int z = 0;
				try
				{
					x = new Integer(args[0]);
				}
				catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[0]));
					return;
				}
				try
				{
					y = new Integer(args[1]);
				}
				catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
					return;
				}
				try
				{
					z = new Integer(args[2]);
				}
				catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[2]));
					return;
				}
				WarpPoint p = new WarpPoint(sender.worldObj.provider.dimensionId, x, y, z, sender.cameraPitch, sender.cameraYaw);
				PlayerInfo.getPlayerInfo(sender).home = p;
				sender.sendChatToPlayer(Localization.format("command.home.confirm", p.getX(), p.getY(), p.getZ()));
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
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
			return getListOfStringsMatchingLastWord(args, "here");
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}
}
