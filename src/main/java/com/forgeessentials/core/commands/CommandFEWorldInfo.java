package com.forgeessentials.core.commands;

import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.util.CommandParserArgs;

public class CommandFEWorldInfo extends ParserCommandBase
{

    @Override
    public void parse(CommandParserArgs arguments)
    {
        arguments.notify("Showing all world provider names:");
        for (World world : DimensionManager.getWorlds())
        {
            arguments.notify("%s - %s", world.provider.getDimension(), world.provider.getClass().getName());
        }
    }

    @Override
    public String getPrimaryAlias()
    {
        return "feworldinfo";
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
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }
}
