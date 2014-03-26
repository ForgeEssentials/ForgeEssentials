package com.forgeessentials.chat.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

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
				EntityPlayerMP player = getPlayer(sender, args[0]);
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
				OutputHandler.chatError(sender, "You don't have permission for that.");
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: " + getSyntaxPlayer(sender));
		}
	}

	// Syntax: /nick <username> [nickname|del]
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (sender instanceof EntityPlayer){
			processCommandPlayer((EntityPlayer)sender, args);
		}
		if (args.length >= 1)
		{
			EntityPlayerMP player = getPlayer(sender, args[0]);
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
				ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <username> [nickname|del]");
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <username> [nickname|del]");
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
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
