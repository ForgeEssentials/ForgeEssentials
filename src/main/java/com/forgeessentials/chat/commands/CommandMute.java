package com.forgeessentials.chat.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMute extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "mute";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayerMP receiver = FunctionHelper.getPlayerForName(sender, args[0]);
			if (receiver == null)
			{
				OutputHandler.chatError(receiver, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", true);
			receiver.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);

			OutputHandler.chatError(sender, Localization.format("command.mute.youMuted", args[0]));
			OutputHandler.chatError(receiver, Localization.format("command.mute.muted", sender.getCommandSenderName()));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayerMP receiver = FunctionHelper.getPlayerForName(sender, args[0]);
			if (receiver == null)
			{
				OutputHandler.chatError(receiver, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", true);
			receiver.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
			OutputHandler.chatError(sender, Localization.format("command.mute.youMuted", args[0]));
			OutputHandler.chatError(receiver, Localization.format("command.mute.muted", sender.getCommandSenderName()));
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
}
