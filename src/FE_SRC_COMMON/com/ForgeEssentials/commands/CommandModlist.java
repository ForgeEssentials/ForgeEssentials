package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class CommandModlist extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "modlist";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		printList(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		printList(sender, args);
	}

	public void printList(ICommandSender sender, String[] args)
	{
		int size = Loader.instance().getModList().size();
		int perPage = 7;
		int pages = (int) Math.ceil(size / (float) perPage);

		int page = args.length == 0 ? 0 : parseIntBounded(sender, args[0], 1, pages) - 1;
		int min = Math.min(page * perPage, size);

		sender.sendChatToPlayer("\u00a72" + Localization.get("command.modlist.header").replaceAll("%p", "" + (page + 1)).replaceAll("%t", "" + pages));

		for (int i = page * perPage; i < min + perPage; i++)
		{
			if (i >= size)
			{
				break;
			}
			ModContainer mod = Loader.instance().getModList().get(i);
			sender.sendChatToPlayer(mod.getName() + " - " + mod.getVersion());
		}
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

}
