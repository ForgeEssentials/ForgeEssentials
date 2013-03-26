package com.ForgeEssentials.playerLogger;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.network.PacketPlayerLogger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Main playerlogger command. Still WIP
 * @author Dries007
 */

public class CommandPl extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "playerlogger";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList(new String[]
		{ "pl" });
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (sender.worldObj.isRemote)
			return;
		if (args.length == 0)
		{
			sender.sendChatToPlayer("You must use /playerlogger enable");
			return;
		}
		else if (args[0].equalsIgnoreCase("enable"))
		{
			int limit = 5;
			if (args.length == 2)
			{
				limit = parseIntWithMin(sender, args[1], 0);
			}
			sender.getEntityData().setBoolean("lb", true);
			sender.getEntityData().setInteger("lb_limit", limit);
			sender.sendChatToPlayer("Click a block and you will get the last " + limit + " changes.");
		}
		else if (args[0].equalsIgnoreCase("disable"))
		{
			sender.getEntityData().setBoolean("lb", false);
		}

		PacketDispatcher.sendPacketToPlayer(new PacketPlayerLogger(sender).getPayload(), (Player) sender);

		// TODO add further stuff.
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{

	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.playerLogger." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "enable", "disable");
		else if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}
}
