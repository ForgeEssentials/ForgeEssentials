package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.core.misc.ItemList;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGive extends ForgeEssentialsCommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "give";
	}
	
	@Override
	public List getCommandAliases()
    {
		return Arrays.asList(new String[] {"i", "item"});
    }

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int id = 1;
		int amount = 64;
		int dam = 0;
		EntityPlayer receiver = sender;
		
		if(args.length == 3)
		{
			receiver = FunctionHelper.getPlayerFromUsername(args[2]);
		}
		
		if(args.length > 1)
		{
			amount = this.parseIntBounded(sender, args[1], 0, 64);
		}
		
		if(args.length < 4)
		{
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[0], false);
			id = idAndMeta[0];
			if(idAndMeta[1] == -1) dam = 0;
			else dam = idAndMeta[1];
			
			ItemStack stack = new ItemStack(id, amount, dam);
			
			try
			{
				sender.sendChatToPlayer("Giving you " + stack.toString());
				receiver.inventory.addItemStackToInventory(stack);
			}
			catch(Exception e)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED + "The server couldn't find the block you where looking for.");
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		int id = 1;
		int amount = 64;
		int dam = 0;
		EntityPlayer receiver;
		
		if(args.length == 3)
		{
			receiver = FunctionHelper.getPlayerFromUsername(args[2]);
		
			amount = this.parseIntBounded(sender, args[1], 0, 64);
		
		
			int[] idAndMeta = FunctionHelper.parseIdAndMetaFromString(args[0], false);
			id = idAndMeta[0];
			if(idAndMeta[1] == -1) dam = 0;
			else dam = idAndMeta[1];
			
			ItemStack stack = new ItemStack(id, amount, dam);
			
			try
			{
				sender.sendChatToPlayer("Giving you " + stack.toString());
				receiver.inventory.addItemStackToInventory(stack);
			}
			catch(Exception e)
			{
				sender.sendChatToPlayer("The server couldn't find the block you where looking for.");
			}
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
		if(args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, ItemList.instance().getItemList());
		}
		else if(args.length == 3)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
    }
}
