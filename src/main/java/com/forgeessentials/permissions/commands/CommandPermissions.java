package com.forgeessentials.permissions.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.CommandParserArgs;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandPermissions extends ForgeEssentialsCommandBuilder
{

    public CommandPermissions(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public final String getPrimaryAlias()
    {
        return "perm";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "fep", "p" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        PermissionCommandParser.parseMain(arguments);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
