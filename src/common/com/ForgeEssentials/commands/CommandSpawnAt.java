package com.ForgeEssentials.commands;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class CommandSpawnAt extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "spawnat";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		try
		{
			if (args.length >= 2)
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[1]);
				if (victim != null)
				{
					int spawnType = new Integer(args[0]);
					spawnType = spawnType < 0 ? 0 : spawnType > 3 ? 3 : spawnType;
					PlayerInfo.getPlayerInfo(victim).spawnType = spawnType;
					OutputHandler.chatConfirmation(victim, Localization.get("message.spawntypechanged") + spawnType);
				} else
					sender.sendChatToPlayer(Localization.get("message.error.noplayer"));
			} else if (args.length == 1)
			{
				int spawnType = new Integer(args[0]);
				spawnType = spawnType < 1 ? 1 : spawnType > 3 ? 3 : spawnType;
				PlayerInfo.getPlayerInfo(sender).spawnType = spawnType;
				OutputHandler.chatConfirmation(sender, Localization.get("message.spawntypechanged") + spawnType);
			} else
				OutputHandler.chatError(sender, Localization.get("message.error.badsyntax") + getSyntaxPlayer(sender));
		} catch (NumberFormatException e)
		{
			OutputHandler.chatError(sender, Localization.get("message.error.nan"));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		try
		{
			if (args.length >= 2)
			{
				EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[1]);
				if (victim != null)
				{
					int spawnType = new Integer(args[0]);
					spawnType = spawnType < 0 ? 0 : spawnType > 3 ? 3 : spawnType;
					PlayerInfo.getPlayerInfo(victim).spawnType = spawnType;
					OutputHandler.chatConfirmation(victim, Localization.get("message.spawntypechanged") + spawnType);
				} else
					sender.sendChatToPlayer(Localization.get("message.error.noplayer"));
			} else
				sender.sendChatToPlayer(Localization.get("message.error.badsyntax") + getSyntaxConsole());
		} catch (NumberFormatException e)
		{
			sender.sendChatToPlayer(Localization.get("message.error.nan"));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
