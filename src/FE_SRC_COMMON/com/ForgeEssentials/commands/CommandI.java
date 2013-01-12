package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;

public class CommandI extends ForgeEssentialsCommandBase {

	@Override
	public String getCommandName()
	{
		return "i";
	}

	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] {"item" });
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int id = 1;
		int amount = 64;
		int dam = 0;
		EntityPlayer receiver = sender;
		
		if (args.length == 2)
		{
			amount = parseIntBounded(sender, args[1], 0, 64);
		}

		if (args.length > 0)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[0], false);
			id = idAndMeta[0];
			if (idAndMeta[1] == -1)
			{
				dam = 0;
			}
			else
			{
				dam = idAndMeta[1];
			}

			ItemStack stack = new ItemStack(id, amount, dam);

			try
			{
				String name = stack.getItem().getItemName();
				if(stack.getItem().getItemName().contains("."))
				{
					name = stack.getItem().getItemName().substring(stack.getItem().getItemName().lastIndexOf(".") + 1);
				}
				sender.sendChatToPlayer("Giving you " + amount + " " + name);
				receiver.inventory.addItemStackToInventory(stack);
			}
			catch (Exception e)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED + "The server couldn't find the block you were looking for.");
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
