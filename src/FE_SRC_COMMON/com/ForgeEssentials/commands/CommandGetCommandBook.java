package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FEChatFormatCodes;

public class CommandGetCommandBook extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "getcommandbook";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "cmdb", "gcmdb" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList pages = new NBTTagList();

		HashMap<String, String> map = new HashMap<String, String>();

		if (sender.inventory.hasItemStack(new ItemStack(Item.writtenBook)))
		{
			int i = 0;
			for (ItemStack e : sender.inventory.mainInventory)
			{
				if (e != null)
				{
					if (e.hasTagCompound())
					{
						if (e.getTagCompound().hasKey("title") && e.getTagCompound().hasKey("author"))
						{
							if (e.getTagCompound().getString("title").equals("CommandBook") && e.getTagCompound().getString("author").equals("ForgeEssentials"))
							{
								sender.inventory.setInventorySlotContents(i, null);
							}
						}
					}
				}
				i++;
			}
		}

		for (Object cmdObj : MinecraftServer.getServer().getCommandManager().getCommands().values().toArray())
		{
			ICommand cmd = (ICommand) cmdObj;

			String text = "\n";
			if (cmd.getCommandAliases() != null && cmd.getCommandAliases().size() != 0)
			{
				text += joinAliases(cmd.getCommandAliases().toArray()) + "\n";
			}
			else
			{
				text += "No aliases.\n";
			}

			text += FEChatFormatCodes.BLACK + cmd.getCommandUsage(sender);

			if (cmd instanceof FEcmdModuleCommands)
			{
				text += "\n";
				text += FEChatFormatCodes.DARKGREY + ((FEcmdModuleCommands) cmd).getCommandPerm();
			}

			if (!text.equals(""))
			{
				map.put(FEChatFormatCodes.DARKAQUA + "/" + cmd.getCommandName(), text);
			}
		}

		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (String name : keys)
		{
			pages.appendTag(new NBTTagString("", name + map.get(name)));
		}

		tag.setString("author", "ForgeEssentials");
		tag.setString("title", "CommandBook");
		tag.setTag("pages", pages);

		ItemStack is = new ItemStack(Item.writtenBook);
		is.setTagCompound(tag);
		sender.inventory.addItemStackToInventory(is);
	}

	public static String joinAliases(Object[] par0ArrayOfObj)
	{
		StringBuilder var1 = new StringBuilder();

		for (int var2 = 0; var2 < par0ArrayOfObj.length; ++var2)
		{
			String var3 = "/" + par0ArrayOfObj[var2].toString();

			if (var2 > 0)
			{
				var1.append(", ");
			}

			var1.append(var3);
		}

		return var1.toString();
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

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.GUESTS;
	}
}
