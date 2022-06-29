package com.forgeessentials.util.questioner;

import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandQuestioner extends ForgeEssentialsCommandBase
{
    private final boolean type;

    public CommandQuestioner(boolean type)
    {
        this.type = type;
    }

    @Override
    public String getPrimaryAlias()
    {
        if (type)
            return "yes";
        else
            return "no";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        if (type)
            return new String[] { "accept", "allow" };
        else
            return new String[] { "decline", "deny" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.questioner";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Questioner.answer(sender, type);
    }

}
