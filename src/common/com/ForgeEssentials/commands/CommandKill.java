package com.ForgeEssentials.commands;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandKill extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "kill";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (victim != null)
			{
				victim.attackEntityFrom(DamageSource.outOfWorld, 1000);
				sender.sendChatToPlayer("Woops, you died. My bad.");
			}
			else
				OutputHandler.chatError(sender, "That player does not exist.");
		}
		else
		{
			sender.attackEntityFrom(DamageSource.outOfWorld, 1000);
			sender.sendChatToPlayer("Woops, you died. My bad.");
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (victim != null)
			{
				victim.attackEntityFrom(DamageSource.outOfWorld, 1000);
				sender.sendChatToPlayer("Woops, you died. My bad.");
			}
			else
				sender.sendChatToPlayer("That player does not exist.");
		}
		else
			sender.sendChatToPlayer("You must specify a player.");
	}

	@Override
	public String getSyntaxConsole()
	{
		return "/kill <Player>";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/kill [Player]";
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
	public String getInfoConsole()
	{
		return "Kills the specified player";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Kills yourself or the specified player";
	}
	
	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands."+getCommandName();
	}

}
