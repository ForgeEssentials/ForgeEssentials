package com.ForgeEssentials.commands;

import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MovingObjectPosition;

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
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.smite.self"));
			} else
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
				if (victim != null)
				{
					sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, sender.posX, sender.posY, sender.posZ));
					sender.sendChatToPlayer(Localization.formatLocalizedString("message.smite.player"));
				} else
					OutputHandler.chatError(sender, Localization.formatLocalizedString("message.error.noPlayerX", args[0]));
			}
		} else
		{
			MovingObjectPosition mop = FunctionHelper.getPlayerLookingSpot(sender, false);
			if (mop == null)
				OutputHandler.chatError(sender, Localization.formatLocalizedString("message.smite.targetError"));
			else
			{
				sender.worldObj.addWeatherEffect(new EntityLightningBolt(sender.worldObj, mop.blockX, mop.blockY, mop.blockZ));
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.smite.ground"));
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
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.smite.player"));
			} else
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.error.noPlayerX", args[0]));
		} else
			sender.sendChatToPlayer(Localization.formatLocalizedString("message.error.specifyPlayer"));
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
