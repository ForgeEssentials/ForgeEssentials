package com.forgeessentials.core.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.util.CommandParserArgs;

public class CommandFEWorldInfo extends ParserCommandBase
{
    @Override
    public void parse(CommandParserArgs arguments)
    {
        arguments.notify("Showing all world provider names:");
        for (World world : DimensionManager.getWorlds())
        {
            arguments.notify("%s - %s", world.provider.getDimensionId(), world.provider.getClass().getName());
        }
    }

    @Override
    public String getCommandName()
    {
        return "feworldinfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/feworldinfo Display the names of all world providers";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.feworldinfo";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }
}
