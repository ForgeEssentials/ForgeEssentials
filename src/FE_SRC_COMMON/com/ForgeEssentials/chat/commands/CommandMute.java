package com.ForgeEssentials.chat.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMute extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "mute";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerFromUsername(args[0]);
			if (receiver == null)
			{
				sender.sendChatToPlayer(args[0] + " is not a valid username");
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", true);
			receiver.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
			sender.sendChatToPlayer(args[0] + " muted.");
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerFromUsername(args[0]);
			if (receiver == null)
			{
				sender.sendChatToPlayer(args[0] + " is not a valid username");
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", true);
			receiver.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
			sender.sendChatToPlayer(args[0] + " muted.");
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Chat.commands." + getCommandName();
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
