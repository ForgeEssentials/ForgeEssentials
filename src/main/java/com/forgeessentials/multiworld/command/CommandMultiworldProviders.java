package com.forgeessentials.multiworld.command;

import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.util.OutputHandler;

/**
 * @author Björn Zeutzheim
 */
public class CommandMultiworldProviders extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwpro";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Print a list of available world providers usable for /mwcreate";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        OutputHandler.chatNotification(commandSender, "Available world providers:");
        for (Entry<String, Integer> provider : ModuleMultiworld.getMultiworldManager().getWorldProviders().entrySet())
        {
            OutputHandler.chatNotification(commandSender, "#" + provider.getValue() + ":" + provider.getKey());
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
        return ModuleMultiworld.PERM_LIST;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
