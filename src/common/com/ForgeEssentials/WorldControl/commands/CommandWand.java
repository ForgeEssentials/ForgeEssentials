package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Item;
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
		int id = player.getCurrentEquippedItem() == null ? 0 : player.getCurrentEquippedItem().itemID;
		PlayerInfo info = PlayerInfo.getPlayerInfo(player.username);
		
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("rebind"))
			{
				info.wandEnabled = true;
				info.wandID = id;
				OutputHandler.chatConfirmation(player, "Wand bound to ID "+id+", "+Item.itemsList[id].getLocalItemName(null));
				return;
			}
			else if (args[0].equalsIgnoreCase("unbind"))
			{
				info.wandEnabled = false;
				player.addChatMessage(OutputHandler.PINK+"Wand unbound from ID "+info.wandID+", "+Item.itemsList[info.wandID].getLocalItemName(null));
				return;
			}
			else
			{
				id = this.interpretIDAndMetaFromString(args[0])[0];
				info.wandEnabled = true;
				info.wandID = id;
				
				OutputHandler.chatConfirmation(player, "Wand bound to ID "+id+", "+Item.itemsList[id].getLocalItemName(null));
			}
		}
		else
		{
			if (info.wandEnabled)
			{
				info.wandEnabled = false;
				player.addChatMessage(OutputHandler.PINK+"Wand unbound from ID "+info.wandID+", "+Item.itemsList[info.wandID].getLocalItemName(null));
				return;
			}
			else
			{
				info.wandEnabled = true;
				info.wandID = id;
				OutputHandler.chatConfirmation(player, "Wand bound to ID "+id+", "+Item.itemsList[id].getLocalItemName(null));
				return;
			}
		}
	}

	@Override
	public String getUsagePlayer(EntityPlayer player)
	{
		return "/"+getCommandName()+" [rebind|unbind|ITEM]";
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
	}
}
