package com.forgeessentials.commands.util;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public abstract class FEcmdModuleCommands extends ForgeEssentialsCommandBase
{

    public static final String COMMANDS_PERM = "fe.commands";

    @Override
    public String getPermissionNode()
    {
        return COMMANDS_PERM + "." + getCommandName();
    }

}
