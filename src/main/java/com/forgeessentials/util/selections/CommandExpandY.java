package com.forgeessentials.util.selections;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.OutputHandler;

public class CommandExpandY extends ForgeEssentialsCommandBase {

	public CommandExpandY()
	{
		return;
	}

	@Override
	public String getCommandName()
	{
		return "/expandY";
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP player, String[] args)
	{
	    Selection sel = SelectionHandler.selectionProvider.getSelection(player);
		if (sel == null)
		    throw new TranslatedCommandException("Invalid selection.");
		SelectionHandler.selectionProvider.setStart(player, sel.getStart().setY(0));
		SelectionHandler.selectionProvider.setEnd(player, sel.getEnd().setY(MinecraftServer.getServer().getBuildLimit()));
		OutputHandler.chatConfirmation(player, "Selection expanded from bottom to top.");
	}

	@Override
	public String getPermissionNode()
	{
		return "fe.core.pos.expandy";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "//expandY: Expands the currently selected area from the top to the bottom of the world.";
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.TRUE;
	}

}
