package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class CommandModlist extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "modlist";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		int size = Loader.instance().getModList().size();
		int perPage = 7;
		int pages = (int) Math.ceil(size / (float) perPage);

		int page = args.length == 0 ? 0 : parseIntBounded(sender, args[0], 1, pages) - 1;
		int min = Math.min(page * perPage, size);

		OutputHandler.chatConfirmation(sender, String.format("--- Showing modlist page %1$d of %2$d ---", page + 1, pages));

		for (int i = page * perPage; i < min + perPage; i++)
		{
			if (i >= size)
			{
				break;
			}
			ModContainer mod = Loader.instance().getModList().get(i);
			ChatUtils.sendMessage(sender, mod.getName() + " - " + mod.getVersion());
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}
	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.GUESTS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/modlist Get a list of all mods running on this server.";
	}

}
