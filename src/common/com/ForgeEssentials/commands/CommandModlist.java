package com.ForgeEssentials.commands;

import java.util.Map;

import net.minecraft.src.CommandNotFoundException;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NumberInvalidException;
import net.minecraft.src.WrongUsageException;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
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
		byte perPage = 7;
		int pages = size / perPage;
		
		int page = args.length == 0 ? 0 : parseIntBounded(sender, args[0], 1, pages);
		int min = Math.min(page * perPage, size);
		
		sender.sendChatToPlayer("\u00a72" + Localization.get("command.modlist.header").replaceAll("%p", "" + page).replaceAll("%t", "" + pages));	
		
		for(int i = (page) * perPage; i < min + perPage; i++)
		{
			if(i >= size) break;
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
