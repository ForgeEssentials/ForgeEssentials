package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.commands.util.VirtualChest;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

/**
 * Opens a configurable virtual chest
 * @author Dries007
 */
public class CommandVirtualchest extends ForgeEssentialsCommandBase
{
	public static int		size	= 54;
	public static String	name	= "Vault 13";

	@Override
	public void doConfig(Configuration config, String category)
	{
		size = config.get(category, "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").getInt(6) * 9;
		name = config.get(category, "VirtualChestName", "Vault 13", "Don't use special stuff....").value;
	}

	@Override
	public String getCommandName()
	{
		return "virtualchest";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "vchest" };
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
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
