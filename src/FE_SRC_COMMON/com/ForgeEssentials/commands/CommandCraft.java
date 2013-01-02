package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.commands.util.ContainerCheatyWorkbench;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandCraft extends ForgeEssentialsCommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "craft";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EntityPlayerMP player = ((EntityPlayerMP)sender);
		player.incrementWindowID();
		player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 1, "Crafting", 9));
        player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
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
