package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

/**
 * Opens your enderchest.
 */
public class CommandEnderchest extends ForgeEssentialsCommandBase
{
    @Override
    public String getPrimaryAlias()
    {
        return "enderchest";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "echest" };
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = sender;
        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        InventoryEnderChest chest = player.getInventoryEnderChest();
        chest.setChestTileEntity(null);
        player.displayGUIChest(chest);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".enderchest";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/enderchest Opens your enderchest.";
    }

}
