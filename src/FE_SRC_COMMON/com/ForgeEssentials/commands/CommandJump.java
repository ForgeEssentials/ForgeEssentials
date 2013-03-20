package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;

public class CommandJump extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "jump";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "j" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		MovingObjectPosition mo = FunctionHelper.getPlayerLookingSpot(sender, false);
		((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(mo.blockX, mo.blockY, mo.blockZ, sender.rotationPitch, sender.rotationYaw);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// NOOP
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.ZONE_ADMINS;
	}
}
