package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandExport
{
	public static void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length > 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
	}

	public static void processCommandConsole(ICommandSender sender, String[] args)
	{
		if(args.length > 0)
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + "");
			return;
		}
		
	}

}
