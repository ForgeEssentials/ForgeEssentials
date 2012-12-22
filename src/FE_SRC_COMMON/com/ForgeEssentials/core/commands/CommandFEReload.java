package com.ForgeEssentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.chat.ConfigChat;
import com.ForgeEssentials.chat.ModuleChat;
import com.ForgeEssentials.commands.ConfigCmd;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.CoreConfig;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.ModuleLauncher;
import com.ForgeEssentials.playerLogger.ConfigPlayerLogger;
import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.Localization;

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
