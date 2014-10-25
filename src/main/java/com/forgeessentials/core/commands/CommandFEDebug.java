package com.forgeessentials.core.commands;

import com.forgeessentials.core.preloader.asm.EventInjector;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandFEDebug extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        OutputHandler.chatNotification(sender, "Injected patches:");
        for (String s : EventInjector.injectedPatches)
        {
            OutputHandler.chatNotification(sender, s);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.core.debug";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "fedebug";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/fedebug Debug FE event injections";
    }


}
