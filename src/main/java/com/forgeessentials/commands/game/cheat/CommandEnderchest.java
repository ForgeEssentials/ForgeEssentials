package com.forgeessentials.commands.game.cheat;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;

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
        return new String[] { "echest" };
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        EntityPlayerMP player = sender;
        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        InventoryEnderChest chest = player.getInventoryEnderChest();
        player.playerNetServerHandler
                .sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, chest.getInventoryName(), chest.getSizeInventory(), true));
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/enderchest Opens your enderchest.";
    }

}
