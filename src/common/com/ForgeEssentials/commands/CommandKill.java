package com.ForgeEssentials.commands;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.Localization;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandKill extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "kill";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (victim != null)
			{
				victim.attackEntityFrom(DamageSource.outOfWorld, 1000);
				victim.sendChatToPlayer(Localization.get("message.killed"));
			} else
				sender.sendChatToPlayer(Localization.get("message.error.noplayer"));
		} else
		{
			sender.attackEntityFrom(DamageSource.outOfWorld, 1000);
			sender.sendChatToPlayer(Localization.get("message.killed"));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayer victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
			if (victim != null)
			{
				victim.attackEntityFrom(DamageSource.outOfWorld, 1000);
				victim.sendChatToPlayer(Localization.get("message.killed"));
			} else
				sender.sendChatToPlayer(Localization.get("message.error.noplayer"));
		} else
			sender.sendChatToPlayer(Localization.get("message.error.badsyntax") + getSyntaxConsole());
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
