package com.forgeessentials.commands.util;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public abstract class FEcmdModuleCommands extends ForgeEssentialsCommandBase
{

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getCommandName();
    }

}
