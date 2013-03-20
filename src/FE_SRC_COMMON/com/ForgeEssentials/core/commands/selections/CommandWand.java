package com.ForgeEssentials.core.commands.selections;

//Depreciated
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandWand extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "/fewand";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		boolean allowed = checkCommandPerm(sender);

		PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
		int currentID = sender.getCurrentEquippedItem() == null ? 0 : sender.getCurrentEquippedItem().itemID;
		int currentDmg = 0;

		if (currentID != 0 && sender.getCurrentEquippedItem().getHasSubtypes())
		{
			currentDmg = sender.getCurrentEquippedItem().getItemDamage();
		}

		String currentName = currentID == 0 ? "your fists" : sender.getCurrentEquippedItem().getDisplayName();
		String wandName = "";
		if (info.wandEnabled)
		{
			if (sender.getCurrentEquippedItem() == null || info.wandID == 0)
			{
				wandName = "your fists";
			}
			else
			{
				wandName = sender.getCurrentEquippedItem().getDisplayName();
			}
		}

		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("rebind"))
			{
				if (allowed)
				{
					info.wandEnabled = true;
					info.wandID = currentID;
					info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
					OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
					return;
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOUSEWAND));
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("unbind"))
			{
				info.wandEnabled = false;
				sender.sendChatToPlayer(FEChatFormatCodes.PINK + "Wand unbound from " + wandName);
				return;
			}
			else
			{
				if (allowed)
				{
					int[] parsed = FunctionHelper.parseIdAndMetaFromString(args[0], false);
					currentID = parsed[0];
					currentDmg = parsed[1];
					info.wandEnabled = true;
					info.wandID = currentID;
					info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
					OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOUSEWAND));
					return;
				}
			}
		}
		else
		{
			if (info.wandEnabled)
			{
				info.wandEnabled = false;
				sender.sendChatToPlayer(FEChatFormatCodes.PINK + "Wand unbound from " + wandName);
				return;
			}
			else
			{
				if (allowed)
				{
					info.wandEnabled = true;
					info.wandID = currentID;
					info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
					OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
					return;
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOUSEWAND));
					return;
				}
			}
		}
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName() + " [rebind|unbind|ITEM]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Toggle the wand";
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
		if (info.wandEnabled)
			return true;
		else
			return checkCommandPerm(player);
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
		return "ForgeEssentials.BasicCommands.wand";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
