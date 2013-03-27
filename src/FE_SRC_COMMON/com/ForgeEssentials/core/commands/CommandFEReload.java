package com.ForgeEssentials.core.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.moduleLauncher.ModuleLauncher;
import com.ForgeEssentials.util.FEChatFormatCodes;

public class CommandFEReload extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "fereload";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		sender.sendChatToPlayer("Reloading ForgeEssentials configs. May not work for all settings!");
		sender.sendChatToPlayer(FEChatFormatCodes.RED + "This is experimental!");
		ModuleLauncher.instance.reloadConfigs(sender);
		sender.sendChatToPlayer("Done!");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		sender.sendChatToPlayer("Reloading ForgeEssentials configs. May not work for all settings!");
		sender.sendChatToPlayer(FEChatFormatCodes.RED + "This is experimental!");
		ModuleLauncher.instance.reloadConfigs(sender);
		sender.sendChatToPlayer("Done!");
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.CoreCommands." + getCommandName();
	}
	
	@Override
	public String getSyntaxConsole()
	{
		return "";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "";
	}

	@Override
	public String getInfoConsole()
	{
		return "Reload FE configs";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Reload FE configs";
	}
	
	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
