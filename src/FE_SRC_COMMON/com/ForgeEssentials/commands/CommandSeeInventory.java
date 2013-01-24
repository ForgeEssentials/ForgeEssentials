package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.packet.Packet100OpenWindow;

import com.ForgeEssentials.commands.util.PlayerInvChest;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Opens other player inventory.
 * 
 * @author Dries007
 * 
 */
public class CommandSeeInventory extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "invsee";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EntityPlayerMP player = (EntityPlayerMP) sender;
		EntityPlayerMP victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
		
		if (player.openContainer != player.inventoryContainer)
		{
			player.closeScreen();
		}
		player.incrementWindowID();

		PlayerInvChest chest = new PlayerInvChest(victim);
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
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
