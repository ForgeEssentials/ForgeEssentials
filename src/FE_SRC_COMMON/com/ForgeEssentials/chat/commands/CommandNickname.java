package com.ForgeEssentials.chat.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandNickname extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "nickname";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList(new String[]
		{ "nick" });
	}

	// Syntax: /nick [nickname|del]
	// Syntax: /nick <username> [nickname|del]
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("del"))
			{
				NBTTagCompound tag = sender.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				tag.removeTag("nickname");
				sender.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
				ChatUtils.sendMessage(sender, Localization.get("command.nickname.self.remove"));
			}
			else
			{
				NBTTagCompound tag = sender.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				tag.setString("nickname", args[0]);
				sender.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
				ChatUtils.sendMessage(sender, Localization.get("command.nickname.self.set").replace("%n", args[0]));
			}
		}
		else if (args.length == 2)
		{
			if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				EntityPlayerMP player = func_82359_c(sender, args[0]);
				if (args[1].equalsIgnoreCase("del"))
				{
					player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).removeTag("nickname");
					ChatUtils.sendMessage(sender, Localization.get("command.nickname.others.remove").replace("%p", args[0]));
				}
				else
				{
					player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setString("nickname", args[1]);
					ChatUtils.sendMessage(sender, Localization.get("command.nickname.others.set").replace("%p", args[0]).replace("%n", args[1]));
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	// Syntax: /nick <username> [nickname|del]
	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayerMP player = func_82359_c(sender, args[0]);
			if (args.length == 2)
			{
				player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setString("nickname", args[1]);
				ChatUtils.sendMessage(sender, "Nickname of player " + player.username + " set to " + args[1]);
			}
			else if (args.length == 1)
			{
				player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).removeTag("nickname");
				ChatUtils.sendMessage(sender, "Nickname of player " + player.username + " removed");
			}
			else
			{
				ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	// Syntax: /nick <username> [nickname|del]
	@Override
	public void processCommandBlock(TileEntityCommandBlock sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayerMP player = func_82359_c(sender, args[0]);
			if (args.length == 2)
			{
				player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setString("nickname", args[1]);
				ChatUtils.sendMessage(sender, "Nickname of player " + player.username + " set to " + args[1]);
			}
			else if (args.length == 1)
			{
				player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).removeTag("nickname");
				ChatUtils.sendMessage(sender, "Nickname of player " + player.username + " removed");
			}
			else
			{
				ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
		return "ForgeEssentials.Chat.commands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
