package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBurn extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "burn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			if (args[0].toLowerCase().equals("me"))
			{
				sender.setFire(15);
				OutputHandler.chatError(sender, Localization.get("command.burn.self"));
			}
			else if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player != null)
				{
					OutputHandler.chatConfirmation(sender, Localization.get("command.burn.player"));
					player.setFire(15);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
		}
		else if (args.length == 2)
		{
			if (args[0].toLowerCase().equals("me"))
			{
				try
				{
					sender.setFire(Integer.parseInt(args[1]));
					OutputHandler.chatError(sender, Localization.get("command.burn.self"));
				}
				catch (NumberFormatException e)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NAN, args[1]));
				}
			}
			else if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
				if (player != null)
				{
					player.setFire(parseIntWithMin(sender, args[1], 0));
					OutputHandler.chatConfirmation(sender, Localization.get("command.burn.player"));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int time = 15;
		if (args.length == 2)
		{
			time = parseIntWithMin(sender, args[1], 0);
		}
		EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
		if (player != null)
		{
			player.setFire(time);
			ChatUtils.sendMessage(sender, Localization.get("command.burn.player"));
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
		}
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
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
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
