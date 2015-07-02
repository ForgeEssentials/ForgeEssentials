package com.forgeessentials.protection.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandProtectionDebug extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "protectdebug";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (ModuleProtection.isDebugMode(sender))
        {
            ModuleProtection.setDebugMode(sender, false);
            ChatOutputHandler.chatConfirmation(sender, "Turned protection debug-mode OFF");
        }
        else
        {
            ModuleProtection.setDebugMode(sender, true);
            ChatOutputHandler.chatConfirmation(sender, "Turned protection debug-mode ON");
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.cmd.protectdebug";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/protectdebug: Toggles protection-module debug-mode";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
