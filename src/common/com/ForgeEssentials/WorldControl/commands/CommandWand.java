package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityCommandBlock;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandWand extends WorldControlCommandBase
{

	@Override
	public String getName()
	{
		return "wand";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(player);
		int currentID = player.getCurrentEquippedItem() == null ? 0 : player.getCurrentEquippedItem().itemID;
		int currentDmg = player.getCurrentEquippedItem() == null ? 0 : player.getCurrentEquippedItem().getItemDamage();
		String currentName = currentID == 0 ? "your fists" : Item.itemsList[info.wandID].getLocalItemName(new ItemStack(info.wandID, 1, info.wandDmg));
		String wandName = "";
		if (info.wandEnabled)
			wandName = info.wandID == 0 ? "your fists" : Item.itemsList[info.wandID].getLocalItemName(new ItemStack(info.wandID, 1, info.wandDmg));

		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("rebind"))
			{
				info.wandEnabled = true;
				info.wandID = currentID;
				info.wandDmg = currentDmg;
				OutputHandler.chatConfirmation(player, "Wand bound to " + currentName);
				return;
			} else if (args[0].equalsIgnoreCase("unbind"))
			{
				info.wandEnabled = false;
				player.addChatMessage(OutputHandler.PINK + "Wand unbound from " + wandName);
				return;
			} else
			{
				currentID = interpretIDAndMetaFromString(args[0])[0];
				currentID = interpretIDAndMetaFromString(args[0])[1];
				info.wandEnabled = true;
				info.wandID = currentID;
				info.wandDmg = currentDmg;
				OutputHandler.chatConfirmation(player, "Wand bound to " + currentName);
			}
		} else
		{
			if (info.wandEnabled)
			{
				info.wandEnabled = false;
				player.addChatMessage(OutputHandler.PINK + "Wand unbound from " + wandName);
				return;
			} else
			{
				info.wandEnabled = true;
				info.wandID = currentID;
				info.wandDmg = currentDmg;
				OutputHandler.chatConfirmation(player, "Wand bound to " + currentName);
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
		// TODO explain what this does
		return null;
	}
}
