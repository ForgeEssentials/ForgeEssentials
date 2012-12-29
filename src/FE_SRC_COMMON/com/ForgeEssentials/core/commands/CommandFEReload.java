package com.ForgeEssentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.util.FEChatFormatCodes;

public class CommandFEReload extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "reload";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer("Reloading ForgeEssentials configs. May not work for all settings!");
		sender.sendChatToPlayer(FEChatFormatCodes.RED + "This is experimental!");
		ModuleLauncher.ReloadConfigs();
		sender.sendChatToPlayer("Done!");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer("Reloading ForgeEssentials configs. May not work for all settings!");
		sender.sendChatToPlayer(FEChatFormatCodes.RED + "This is experimental!");
		ModuleLauncher.ReloadConfigs();
		sender.sendChatToPlayer("Done!");
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
		return null;
	}

}
