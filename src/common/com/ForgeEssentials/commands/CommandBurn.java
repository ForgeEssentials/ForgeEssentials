package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBurn extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "burn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 2)
		{
			if (args[0].toLowerCase().equals("me"))
			{
				sender.setFire(Integer.parseInt(args[1]));
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.burn.self"));
			} else
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
				if (victim != null)
				{
					victim.setFire(Integer.parseInt(args[1]));
					sender.sendChatToPlayer(Localization.formatLocalizedString("message.burn.player"));
				} else
					OutputHandler.chatError(sender, Localization.formatLocalizedString("message.error.noPlayerX", args[0]));
			}
		} else
		{
			OutputHandler.chatError(sender, (Localization.get("message.error.badsyntax") + getSyntaxPlayer(sender)));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (victim != null)
			{
				victim.setFire(Integer.parseInt(args[1]));
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.burn.player"));
			} else
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.error.noPlayerX", args[0]));
		} else
		{
			sender.sendChatToPlayer(Localization.get("message.error.badsyntax") + getSyntaxConsole());	
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
