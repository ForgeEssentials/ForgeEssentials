package com.forgeessentials.util.selections;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

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
		if (SelectionHandler.selectionProvider.getPoint1(player) == null || SelectionHandler.selectionProvider.getPoint2(player) == null)
		{
			OutputHandler.chatError(player, "Invalid selection.");
			return;
		}
		SelectionHandler.selectionProvider.getPoint1(player).setY(0);
		SelectionHandler.selectionProvider.getPoint2(player).setY(255);
		OutputHandler.chatConfirmation(player, "Selection expanded to world height.");
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
