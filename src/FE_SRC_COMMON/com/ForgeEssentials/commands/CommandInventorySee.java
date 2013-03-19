package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.packet.Packet100OpenWindow;

import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.commands.util.InvSeeMisk;
import com.ForgeEssentials.commands.util.PlayerInvChest;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Opens other player inventory.
 * @author Dries007
 */
public class CommandInventorySee extends FEcmdModuleCommands
{
	private InvSeeMisk	invSeeMisk;

	public CommandInventorySee()
	{
		invSeeMisk = new InvSeeMisk();
		TickRegistry.registerTickHandler(invSeeMisk, Side.SERVER);
	}

	@Override
	public String getCommandName()
	{
		return "invsee";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;
		EntityPlayerMP player = (EntityPlayerMP) sender;
		EntityPlayerMP victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);

		if (player.openContainer != player.inventoryContainer)
		{
			player.closeScreen();
		}
		player.incrementWindowID();

		PlayerInvChest chest = new PlayerInvChest(victim, (EntityPlayerMP) sender);
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

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

}
