package com.ForgeEssentials.commands;

import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;

import com.ForgeEssentials.WorldControl.FunctionHelper;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSmite extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "smite";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			if (args[0].toLowerCase().equals("me"))
			{
				sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, sender.posX, sender.posY, sender.posZ));
				sender.sendChatToPlayer("Was that really a good idea?");
			} else
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
				if (victim != null)
				{
					sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, sender.posX, sender.posY, sender.posZ));
					sender.sendChatToPlayer("You should feel bad about doing that.");
				} else
					OutputHandler.chatError(sender, "That player does not exist.");
			}
		} else
		{
			MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(sender, false);
			if (mop == null)
				OutputHandler.chatError(sender, "LOOK AT THE GROUND. Also, the range is 500 blocks.");
			else
			{
				sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, mop.blockX, mop.blockY, mop.blockZ));
				sender.sendChatToPlayer("I hope that didn't start a fire.");
			}
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
				victim.worldObj.addWeatherEffect(new EntityLightningBolt(victim.worldObj, victim.posX, victim.posY, victim.posZ));
				sender.sendChatToPlayer("You should feel bad about doing that.");
			} else
				sender.sendChatToPlayer("That player does not exist.");
		} else
			sender.sendChatToPlayer("You must specify a player.");
	}

	@Override
	public String getSyntaxConsole()
	{
		return "/smite [me|<Player>]";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/smite <Player>";
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
		return "Strike the specified player with lightning";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Strike where you are looking, yourself, or another player with lightning";
	}

}
