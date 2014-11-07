package com.forgeessentials.worldedit.compat;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.sk89q.worldedit.util.command.CommandMapping;

public class WECommandWrapper extends ForgeEssentialsCommandBase {
    
    public static final String PERM_OTHER = "worldedit.other";
    
    private CommandMapping commandMapping;
    
    public WECommandWrapper(CommandMapping commandMapping) {
        this.commandMapping = commandMapping;
    }
    
    @Override
    public String getCommandName()
    {
        return commandMapping.getPrimaryAlias();
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList(commandMapping.getAllAliases());
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "/" + commandMapping.getPrimaryAlias() + " " + commandMapping.getDescription().getUsage();
    }

    @Override
    public String getPermissionNode()
    {
        List<String> permissions = commandMapping.getDescription().getPermissions();
        if (permissions.isEmpty())
            return PERM_OTHER;
        else
            return permissions.get(0);
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

}