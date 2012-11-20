package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.OutputHandler;

public class CommandWand extends WorldControlCommandBase
{

	@Override
	public String getName()
	{
		return "wand";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender);
		int currentID = sender.getCurrentEquippedItem() == null ? 0 : sender.getCurrentEquippedItem().itemID;
		int currentDmg = 0;
		
		if (currentID != 0 && sender.getCurrentEquippedItem().getHasSubtypes())
			currentDmg = sender.getCurrentEquippedItem().getItemDamage();
		
		String currentName = currentID == 0 ? "your fists" : sender.getCurrentEquippedItem().getDisplayName();
		String wandName = "";
		if (info.wandEnabled)
			wandName = info.wandID == 0 ? "your fists" : sender.getCurrentEquippedItem().getDisplayName();

		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("rebind"))
			{
				info.wandEnabled = true;
				info.wandID = currentID;
				info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
				OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
				return;
			} else if (args[0].equalsIgnoreCase("unbind"))
			{
				info.wandEnabled = false;
				sender.sendChatToPlayer(OutputHandler.PINK + "Wand unbound from " + wandName);
				return;
			} else
			{
				currentID = interpretIDAndMetaFromString(args[0])[0];
				currentDmg = interpretIDAndMetaFromString(args[0])[1];
				info.wandEnabled = true;
				info.wandID = currentID;
				info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
				OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
			}
		} else
		{
			if (info.wandEnabled)
			{
				info.wandEnabled = false;
				sender.sendChatToPlayer(OutputHandler.PINK + "Wand unbound from " + wandName);
				return;
			} else
			{
				info.wandEnabled = true;
				info.wandID = currentID;
				info.wandDmg = currentDmg == -1 ? 0 : currentDmg;
				OutputHandler.chatConfirmation(sender, "Wand bound to " + currentName);
				return;
			}
		}
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
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
}
