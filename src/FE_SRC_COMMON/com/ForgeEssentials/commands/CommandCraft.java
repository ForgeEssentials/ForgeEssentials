package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet100OpenWindow;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.ContainerCheatyWorkbench;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;

public class CommandCraft extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "craft";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EntityPlayerMP player = (EntityPlayerMP) sender;
		player.incrementWindowID();
		player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 1, "Crafting", 9, true));
		player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
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
		return RegGroup.MEMBERS;
	}
}
