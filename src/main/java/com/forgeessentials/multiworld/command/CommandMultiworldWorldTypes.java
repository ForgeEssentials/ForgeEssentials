package com.forgeessentials.multiworld.command;

import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.MultiworldManager;
import com.forgeessentials.util.OutputHandler;

/**
 * @author Geoffrey McRae
 */
public class CommandMultiworldWorldTypes extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwtypes";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Print a list of available world types usable for /mwcreate";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        OutputHandler.chatNotification(commandSender, "Available world types:");
        for (String worldType : ModuleMultiworld.getMultiworldManager().getWorldTypes().keySet())
        {
            OutputHandler.chatNotification(commandSender, "  " + worldType);
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