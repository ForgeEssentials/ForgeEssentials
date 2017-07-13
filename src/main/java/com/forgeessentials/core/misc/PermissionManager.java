package com.forgeessentials.core.misc;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Transition class to the new Permissions API
 */
public class PermissionManager
{
    protected static Map<ICommand, String> commandPermissions = new WeakHashMap<>();
    public static String getCommandPermission(ICommand command)
    {
        if (command instanceof PermissionObject)
        {
            String permission = ((PermissionObject) command).getPermissionNode();
            if (permission != null)
                return permission;
        }
        String permission = commandPermissions.get(command);
        if (permission != null)
            return permission;
        return "command." + command.getName();
    }

    public static DefaultPermissionLevel getCommandLevel(ICommand command)
    {
        if (command instanceof PermissionObject)
            return ((PermissionObject) command).getPermissionLevel();
        if (command instanceof CommandBase)
            return fromIntegerLevel(((CommandBase) command).getRequiredPermissionLevel());
        return DefaultPermissionLevel.OP;
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     * This method should not be called directly, but instead is called by forge upon registration of a new command
     *
     * @param command
     */
    public static void registerCommandPermission(ICommand command)
    {
        PermissionAPI.registerNode(getCommandPermission(command), getCommandLevel(command), "");
    }

    /**
     * This method allows you to register permissions for commands that cannot implement the PermissionObject interface
     * for any reason.
     *
     * @param command
     * @param permission
     * @param permissionLevel
     */
    public static void registerCommandPermission(ICommand command, String permission, DefaultPermissionLevel permissionLevel)
    {
        commandPermissions.put(command, permission);
        PermissionAPI.registerNode(permission, permissionLevel, "");
    }

    /**
     * This method allows you to register permissions for commands that cannot implement the PermissionObject interface
     * for any reason.
     *
     * @param command
     * @param permission
     */
    public static void registerCommandPermission(ICommand command, String permission)
    {
        registerCommandPermission(command, permission, getCommandLevel(command));
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     * TODO This method should be removed in the PR
     *
     * @param command a command
     */
    public static void registerCommandPermissions()
    {
        @SuppressWarnings("unchecked")
        Map<String, ICommand> commands = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands();
        for (ICommand command : commands.values())
            if (!commandPermissions.containsKey(command))
                registerCommandPermission(command);
    }

    public static DefaultPermissionLevel fromIntegerLevel(int value)
    {
        switch (value)
        {
        case 0:
            return DefaultPermissionLevel.ALL;
        case 1:
        case 2:
        case 3:
        case 4:
            return DefaultPermissionLevel.OP;
        default:
            return DefaultPermissionLevel.NONE;
        }

    }
    public interface PermissionObject
    {
        public String getPermissionNode();

        public DefaultPermissionLevel getPermissionLevel();
    }
}
