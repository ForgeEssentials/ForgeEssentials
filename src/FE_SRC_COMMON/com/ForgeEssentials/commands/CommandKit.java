package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 * @author Dries007
 */

public class CommandKit extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "kit";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		NBTTagCompound kitData = DataStorage.getData("kitdata");
		/*
		 * Print kits
		 */
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization.get(Localization.KIT_LIST));
			String msg = "";
			for (Object temp : kitData.getTags())
			{
				NBTTagCompound kit = (NBTTagCompound) temp;
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + kit.getName())))
				{
					msg = kit.getName() + ", " + msg;
				}
			}
			sender.sendChatToPlayer(msg);
			return;
		}
		/*
		 * Give kit
		 */
		if (args.length == 1)
		{
			if (kitData.hasKey(args[0].toLowerCase()))
			{
				if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
				{
					giveKit(sender, kitData.getCompoundTag(args[0].toLowerCase()));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.KIT_NOTEXISTS));
			}
			return;
		}
		/*
		 * Make kit
		 */
		if (args[1].equalsIgnoreCase("set") && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
		{
			if (args.length == 3)
			{
				if (!kitData.hasKey(args[0].toLowerCase()))
				{
					int cooldown = parseIntWithMin(sender, args[2], 0);
					makeKit(sender, args[0].toLowerCase(), cooldown);
					sender.sendChatToPlayer(Localization.get(Localization.KIT_MADE).replaceAll("%c", "" + FunctionHelper.parseTime(cooldown)));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.KIT_ALREADYEXISTS));
				}
				return;
			}
		}

		/*
		 * Delete kit
		 */
		if (args[1].equalsIgnoreCase("del") && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
		{
			if (args.length == 2)
			{
				if (kitData.hasKey(args[0].toLowerCase()))
				{
					kitData.removeTag(args[0].toLowerCase());
					sender.sendChatToPlayer(Localization.get(Localization.KIT_REMOVED));
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.KIT_NOTEXISTS));
				}
				return;
			}
		}

		/*
		 * You're doing it wrong!
		 */
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
	}

	public void makeKit(EntityPlayer player, String name, int cooldown)
	{
		NBTTagCompound kitData = DataStorage.getData("kitdata");
		NBTTagCompound kit = new NBTTagCompound(name);

		kit.setInteger("cooldown", cooldown);

		/*
		 * Main inv.
		 */
		NBTTagList items = new NBTTagList();
		for (ItemStack stack : player.inventory.mainInventory)
		{
			if (stack != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				stack.writeToNBT(item);
				items.appendTag(item);
			}
		}
		kit.setTag("items", items);

		/*
		 * Armor
		 */
		for (int i = 0; i < 4; i++)
		{
			ItemStack stack = player.inventory.armorInventory[i];
			if (stack != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				stack.writeToNBT(item);
				kit.setCompoundTag("armor" + i, item);
			}
		}

		kitData.setCompoundTag(name, kit);
		DataStorage.setData("kitdata", kitData);
	}

	public void giveKit(EntityPlayer player, NBTTagCompound kit)
	{
		if (PlayerInfo.getPlayerInfo(player.username).kitCooldown.containsKey(kit.getName()))
		{
			player.sendChatToPlayer(Localization.get(Localization.KIT_STILLINCOOLDOWN).replaceAll("%c", "" + FunctionHelper.parseTime(PlayerInfo.getPlayerInfo(player.username).kitCooldown.get(kit.getName()))));
		}
		else
		{
			player.sendChatToPlayer(Localization.get(Localization.KIT_DONE));

			if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, TickHandlerCommands.BYPASS_KIT_COOLDOWN)))
			{
				PlayerInfo.getPlayerInfo(player.username).kitCooldown.put(kit.getName(), kit.getInteger("cooldown"));
			}

			/*
			 * Main inv.
			 */
			for (int i = 0; kit.getTagList("items").tagCount() > i; i++)
			{
				ItemStack stack = new ItemStack(0, 0, 0);
				stack.readFromNBT((NBTTagCompound) kit.getTagList("items").tagAt(i));
				player.inventory.addItemStackToInventory(stack);
			}

			/*
			 * Armor
			 */
			for (int i = 0; i < 4; i++)
			{
				if (kit.hasKey("armor" + i))
				{
					ItemStack stack = new ItemStack(0, 0, 0);
					stack.readFromNBT(kit.getCompoundTag("armor" + i));
					if (player.inventory.armorInventory[i] == null)
					{
						player.inventory.armorInventory[i] = stack;
					}
					else
					{
						player.inventory.addItemStackToInventory(stack);
					}
				}
			}
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

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		NBTTagCompound warps = DataStorage.getData("kitdata");
		Iterator warpsIt = warps.getTags().iterator();
		List<String> list = new ArrayList<String>();
		while (warpsIt.hasNext())
		{
			NBTTagCompound buffer = (NBTTagCompound) warpsIt.next();
			list.add(buffer.getName());
		}

		list.add("set");
		list.add("del");

		if (args.length == 1)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, list);
		}
		else
		{
			return null;
		}
	}

}
