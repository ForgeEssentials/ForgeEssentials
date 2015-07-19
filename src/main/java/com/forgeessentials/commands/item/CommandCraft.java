package com.forgeessentials.commands.item;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.util.FEcmdModuleCommands;

public class CommandCraft extends FEcmdModuleCommands
{
    @Override
    public String getCommandName()
    {
        return "craft";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = sender;
        player.displayGui(new BlockWorkbench.InterfaceCraftingTable(player.worldObj, player.playerLocation));
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/craft Open a crafting window.";
    }
}
