package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MovingObjectPosition;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandJump extends ForgeEssentialsCommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "jump";
	}
	
	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] {"j"});
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
		//NOOP
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
}
