package com.forgeessentials.core.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FEChatFormatCodes;

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
		ChatUtils.sendMessage(sender, "Reloading ForgeEssentials configs. May not work for all settings!");
		ChatUtils.sendMessage(sender, FEChatFormatCodes.RED + "This is experimental!");
		ModuleLauncher.instance.reloadConfigs(sender);
		ChatUtils.sendMessage(sender, "Done!");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		ChatUtils.sendMessage(sender, "Reloading ForgeEssentials configs. May not work for all settings!");
		ChatUtils.sendMessage(sender, FEChatFormatCodes.RED + "This is experimental!");
		ModuleLauncher.instance.reloadConfigs(sender);
		ChatUtils.sendMessage(sender, "Done!");
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
