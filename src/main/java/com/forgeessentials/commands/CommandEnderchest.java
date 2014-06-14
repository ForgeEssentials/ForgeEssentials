package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.network.packet.Packet100OpenWindow;

/**
 * Opens your enderchest.
 *
 * @author Dries007
 */
public class CommandEnderchest extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "enderchest";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[]
                { "echest" };
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
        player.playerNetServerHandler
                .sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 0, chest.getInvName(), chest.getSizeInventory(), true));
        player.openContainer = new ContainerChest(player.inventory, chest);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // TODO Auto-generated method stub
        return "/enderchest Opens your enderchest.";
    }

}
