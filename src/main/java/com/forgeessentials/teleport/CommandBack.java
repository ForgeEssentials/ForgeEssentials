package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.TeleportCenter;
import com.forgeessentials.util.AreaSelector.WarpPoint;

public class CommandBack extends FEcmdModuleCommands
{
	public static List justDied = new ArrayList<String>();
	@Override
	public String getCommandName()
	{
		return "back";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(justDied.contains(sender.username))
		{
			if(APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.BasicCommands.back.ondeath")))
			{
				PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
				if (info.back != null)
				{
					WarpPoint death = info.back;
					info.back = new WarpPoint(sender);
					EntityPlayerMP player = (EntityPlayerMP) sender;
					TeleportCenter.addToTpQue(death, player);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get("command.back.noback"));
				}
				justDied.remove(sender.username);
				return;
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("command.back.nodeath"));
			}
		}
		else if(APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, "ForgeEssentials.BasicCommands.back.ontp")))
		{
			PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
			if (info.back != null)
			{
				WarpPoint back = info.back;
				info.back = new WarpPoint(sender);
				EntityPlayerMP player = (EntityPlayerMP) sender;
				TeleportCenter.addToTpQue(back, player);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("command.back.noback"));
			}
			return;
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
