package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
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
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        ServerPlayerEntity player = sender;
        if (player.containerMenu != player.inventoryMenu)
        {
            player.closeContainer();
        }
        player.nextContainerCounter();

        //chest.setChestTileEntity(null);
        player.getEnderChestInventory().startOpen(player);
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

}
