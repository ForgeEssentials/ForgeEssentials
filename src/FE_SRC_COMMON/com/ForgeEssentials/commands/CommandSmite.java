package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

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
				sender.sendChatToPlayer(Localization.get(Localization.SMITE_SELF));
			} else
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
				if (victim != null)
				{
					victim.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, sender.posX, sender.posY, sender.posZ));
					sender.sendChatToPlayer(Localization.get(Localization.SMITE_PLAYER));
				} else
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		} else
		{
			MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(sender, false);
			if (mop == null)
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_TARGET));
			else
			{
				sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, mop.blockX, mop.blockY, mop.blockZ));
				sender.sendChatToPlayer(Localization.get(Localization.SMITE_GROUND));
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
				sender.sendChatToPlayer(Localization.get(Localization.SMITE_PLAYER));
			} else
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
		} else
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	if(args.length == 1)
    	{
    		return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
    	}
    	else
    	{
    		return null;
    	}
    }

}
