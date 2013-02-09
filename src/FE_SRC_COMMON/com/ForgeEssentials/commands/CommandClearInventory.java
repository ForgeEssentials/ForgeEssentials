package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandClearInventory extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "clear";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "ci" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			int var6 = sender.inventory.clearInventory(-1, -1);
			sender.inventoryContainer.detectAndSendChanges();
			sender.sendChatToPlayer("Cleared inventory.");
		}
		else if (args.length == 1 && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			EntityPlayer victim = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			int var6 = victim.inventory.clearInventory(-1, -1);
			victim.inventoryContainer.detectAndSendChanges();
			victim.sendChatToPlayer("Inventory cleared by " + sender.username);
			sender.sendChatToPlayer("Cleared inventory of " + victim.username);
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer victim = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (PlayerSelector.hasArguments(args[0]))
			{
				victim = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			int var6 = victim.inventory.clearInventory(-1, -1);
			victim.inventoryContainer.detectAndSendChanges();
			String senderName = (sender instanceof TileEntityCommandBlock ? "CommandBlock @ (" + ((TileEntityCommandBlock) sender).xCoord + "," + ((TileEntityCommandBlock) sender).yCoord + "," + ((TileEntityCommandBlock) sender).zCoord + ")."
					: "the console");
			victim.sendChatToPlayer("Inventory cleared by " + senderName);
			sender.sendChatToPlayer("Cleared inventory of " + victim.username);
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands.clear.self";
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, ItemList.instance().getItemList());
		}
		else if (args.length == 3)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
