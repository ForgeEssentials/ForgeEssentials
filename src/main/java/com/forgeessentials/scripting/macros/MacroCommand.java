package com.forgeessentials.scripting.macros;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class MacroCommand extends ForgeEssentialsCommandBase
{
    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.script.macro";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "macro";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return null;
    }
}
