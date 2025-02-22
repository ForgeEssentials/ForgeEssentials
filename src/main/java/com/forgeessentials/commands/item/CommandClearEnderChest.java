package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.CommandParserArgs;

public class CommandClearEnderChest extends ForgeEssentialsCommandBase
{
    @Override public String getCommandName()
    {
        return "clearender";
    }

    @Override public String getCommandUsage(ICommandSender sender)
    {
        return "/clearender: Clears the players ender chest";
    }

    @Override public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".clearender";
    }

    @Override public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        CommandParserArgs args1 = new CommandParserArgs(this, args, sender, false);
        UserIdent subject = args1.parsePlayer(true, true);
        subject.getPlayer().getInventoryEnderChest().clear();
    }
}
