package com.forgeessentials.chat.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandUnmute extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "unmute";
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerForName(sender, args[0]);
			if (receiver == null)
			{
				OutputHandler.chatError(receiver, String.format("Player %s does not exist, or is not online.", args[0]));
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", false);
			receiver.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);

			OutputHandler.chatError(sender, String.format("command.unmute.youMuted", args[0]));
			OutputHandler.chatError(receiver, String.format("command.unmute.muted", sender.getCommandSenderName()));
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
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/unmute <player> Unmutes a player.";
	}
}
