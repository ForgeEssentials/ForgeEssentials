package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.network.packet.Packet100OpenWindow;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandVirtualchest extends ForgeEssentialsCommandBase
{
	public static boolean useAlias = true;
	public static int size = 54;
	public static String name = "Vault 13";

	@Override
	public String getCommandName()
	{
		return "virtualchest";
	}
	
	@Override
	public List getCommandAliases()
    {
		if(useAlias)
			return Arrays.asList(new String[] {"vchest"});
		return null;
    }

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EntityPlayerMP player = (EntityPlayerMP) sender;
		if (player.openContainer != player.inventoryContainer)
    	{
    		player.closeScreen();
    	}
		player.incrementWindowID();
		
		VirtualChest chest = new VirtualChest(player);
		player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 0, name, size));
    	player.openContainer = new ContainerChest(player.inventory, chest);
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
