package com.forgeessentials.core.commands.selections;

//Depreciated
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

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
					OutputHandler.chatError(sender, "Could not bind wand to " + currentName);
					return;
				}
			}
			else if (args[0].equalsIgnoreCase("unbind"))
			{
				info.wandEnabled = false;
				ChatUtils.sendMessage(sender, EnumChatFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
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
					OutputHandler.chatError(sender, "Could not bind wand to " + currentName);
					return;
				}
			}
		}
		else
		{
			if (info.wandEnabled)
			{
				info.wandEnabled = false;
				ChatUtils.sendMessage(sender, EnumChatFormatting.LIGHT_PURPLE + "Wand unbound from " + wandName);
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
					OutputHandler.chatError(sender, "Could not bind wand to " + currentName);
					return;
				}
			}
		}
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
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "fe.core.pos.wand";
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [rebind|unbind|ITEM] Toggles the wand";
	}
	
	@Override
	public RegGroup getReggroup() {
		// TODO Auto-generated method stub
		return RegGroup.MEMBERS;
	}
}
