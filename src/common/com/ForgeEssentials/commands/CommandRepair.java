package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ItemStack;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRepair extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "repair";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			ItemStack item = sender.getHeldItem();
			
			if (item == null)
			{
				OutputHandler.chatError(sender, Localization.formatLocalizedString("message.error.noItemSelf"));
			}
			
			item.setItemDamage(0);
			
		} 
		else if (args.length == 1)
		{
			EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			
			if (target != null)
			{
				ItemStack item = target.getHeldItem();

				if (item != null)
				{
					item.setItemDamage(0);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.formatLocalizedString("message.error.noItemTarget"));
				}
			}
			else
			{
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.error.noPlayerX", args[0]));
			}
		} 
		else
		{
			OutputHandler.chatError(sender, (Localization.get("message.error.badsyntax") + getSyntaxPlayer(sender)));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			
			if (target != null)
			{
				ItemStack item = target.getHeldItem();

				if (item != null)
				{
					item.setItemDamage(0);
				}
				else
				{
					sender.sendChatToPlayer(Localization.formatLocalizedString("message.error.noItemTarget"));
				}
			}
			else
			{
				sender.sendChatToPlayer(Localization.formatLocalizedString("message.error.noPlayerX", args[0]));
			}
		} 
		else
		{
			sender.sendChatToPlayer(Localization.get("message.error.badsyntax") + getSyntaxConsole());
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
