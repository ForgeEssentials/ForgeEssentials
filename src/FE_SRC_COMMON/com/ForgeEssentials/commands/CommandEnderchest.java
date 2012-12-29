package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.network.packet.Packet100OpenWindow;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

/**
 * Opens your enderchest.
 * @author Dries007
 *
 */
public class CommandEnderchest extends ForgeEssentialsCommandBase
{
	public static boolean useAlias = true;

	@Override
	public String getCommandName()
	{
		return "enderchest";
	}
	
	@Override
	public List getCommandAliases()
    {
		if(useAlias)
			return Arrays.asList(new String[] {"echest"});
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
    	
    	InventoryEnderChest chest = player.getInventoryEnderChest();
		player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 0, chest.getInvName(), chest.getSizeInventory()));
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
