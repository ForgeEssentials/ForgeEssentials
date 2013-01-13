package com.ForgeEssentials.playerLogger;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Main playerlogger command. Still WIP
 * 
 * @author Dries007
 * 
 */

public class CommandPl extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "playerlogger";
	}

	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] {"pl"});
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(sender.worldObj.isRemote) return;
		if (args.length == 0)
		{
			// TODO INFO
			
			return;
		}
		if (args[0].equalsIgnoreCase("get"))
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
		return "";
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "get", "rollback");
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
