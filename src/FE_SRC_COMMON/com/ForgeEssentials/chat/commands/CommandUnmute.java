package com.ForgeEssentials.chat.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandUnmute extends ForgeEssentialsCommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "unmute";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length == 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerFromUsername(args[0]);
			if(receiver == null)
			{
				sender.sendChatToPlayer(args[0] + " is not a valid username");
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(receiver.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", false);
			receiver.getEntityData().setCompoundTag(receiver.PERSISTED_NBT_TAG, tag);
			sender.sendChatToPlayer(args[0] + " unmuted.");
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if(args.length == 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerFromUsername(args[0]);
			if(receiver == null)
			{
				sender.sendChatToPlayer(args[0] + " is not a valid username");
				return;
			}
			NBTTagCompound tag = receiver.getEntityData().getCompoundTag(receiver.PERSISTED_NBT_TAG);
			tag.setBoolean("mute", false);
			receiver.getEntityData().setCompoundTag(receiver.PERSISTED_NBT_TAG, tag);
			sender.sendChatToPlayer(args[0] + " unmuted.");
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
    	if(args.length == 1)
    	{
    		return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
    	}
    	else
    	{
    		return null;
    	}
    }
}
