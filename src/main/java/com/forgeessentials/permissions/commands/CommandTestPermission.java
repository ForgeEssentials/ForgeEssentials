package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

public class CommandTestPermission extends ForgeEssentialsCommandBase {

	@Override
	public final String getCommandName()
	{
		return "ptest";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		return true;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length != 1)
			throw new CommandException("Invalid arguments");
		if (PermissionsManager.checkPermission(sender, args[0]))
			OutputHandler.chatConfirmation(sender, args[0] + " = true");
		else
			OutputHandler.chatError(sender, args[0] + " = false");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		OutputHandler.chatError(sender, "Cannot test permissions from console!");
	}

	@Override
	public String getPermissionNode()
	{
		return "fe.perm.test";
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
//		PermResult result = APIRegistry.perms.checkPermResult(player, getPermissionNode(, true));
//		return result.equals(PermResult.DENY) ? false : true;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{

		return "/ptest <perm> Test FE permissions.";
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{

		return RegisteredPermValue.OP;
	}

}
