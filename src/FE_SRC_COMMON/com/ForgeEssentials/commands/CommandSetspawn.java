package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetspawn extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "setspawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 3)
		{
			int x = parseInt(sender, args[0]);
			int y = parseInt(sender, args[1]);
			int z = parseInt(sender, args[2]);
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(x, y, z);
			sender.sendChatToPlayer(Localization.get("command.setspawn.set"));
		}
		else
		{
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint((int) sender.posX, (int) sender.posY, (int) sender.posZ);
			sender.sendChatToPlayer(Localization.get("command.setspawn.set"));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 3)
		{
			int x = parseInt(sender, args[0]);
			int y = parseInt(sender, args[1]);
			int z = parseInt(sender, args[2]);
			FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[0].provider.setSpawnPoint(x, y, z);
			sender.sendChatToPlayer(Localization.get("command.setspawn.set"));
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
