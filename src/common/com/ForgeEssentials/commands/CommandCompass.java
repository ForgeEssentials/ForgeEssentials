package com.ForgeEssentials.commands;

import net.minecraft.src.Direction;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.MathHelper;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;

public class CommandCompass extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "compass";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		int rotationYaw = MathHelper.floor_double((double)(sender.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		sender.sendChatToPlayer(Localization.formatLocalizedString("command.compass.direction") + Direction.directions[rotationYaw]);
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
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

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) 
	{
		
	}
}