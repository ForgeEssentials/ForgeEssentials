package com.forgeessentials.core.misc;

import java.util.Map;
import java.util.WeakHashMap;

//import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Transition class to the new Permissions API
 */
public class PermissionManager
{
    /*
     * First String is command name
     * Second string is the node
    */
    protected static Map<String, String> commandPermissions = new WeakHashMap<>();

    public static String getCommandPermission(String commandName)
    {
        String permission = commandPermissions.get(commandName);
        if (permission != null)
            return permission;
        return "command." + commandName;
    }

    public static DefaultPermissionLevel getCommandLevel(String commandName)
    {
        CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();

        for (CommandNode<CommandSource> commandNode : dispatcher.getRoot().getChildren()) {
            if (commandNode.getRequirement() != null) {
                String permission = stripNode(commandNode.getRequirement().toString());

                if(commandName == commandNode.getUsageText().substring(1)) {
                    DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(permission);
                }
                System.out.println("Command: " + commandNode.getUsageText() + " - Permission: " + permission);
            }
        }
        return DefaultPermissionLevel.OP;
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     * This method should not be called directly, but instead is called by forge upon registration of a new command
     *
     * @param command
     */
    public static void registerCommandPermission(String commandName)
    {
        PermissionAPI.registerNode(getCommandPermission(commandName), getCommandLevel(commandName), "");
    }

    /**
     * This method allows you to register permissions for commands that cannot implement the PermissionObject interface for any reason.
     *
     * @param command
     * @param permission
     * @param permissionLevel
     */
    public static void registerCommandPermission(String commandName, String permission, DefaultPermissionLevel permissionLevel)
    {
        commandPermissions.put(commandName, permission);
        PermissionAPI.registerNode(permission, permissionLevel, "");
    }

    /**
     * This method allows you to register permissions for commands that cannot implement the PermissionObject interface for any reason.
     *
     * @param command
     * @param permission
     */
    public static void registerCommandPermission(String commandName, String permission)
    {
        registerCommandPermission(commandName, permission, getCommandLevel(commandName));
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     * TODO This method should be removed in the PR
     *
     * @param command
     *            a command
     */
    public static void registerCommandPermissions()
    {
        CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();

        for (CommandNode<CommandSource> commandNode : dispatcher.getRoot().getChildren()) {
            if (commandNode.getRequirement() != null) {
                String permission = stripNode(commandNode.getRequirement().toString());

                System.out.println("Found command: " + commandNode.getUsageText().substring(1) + " - Permission: " + permission);
            }
            if (!commandPermissions.containsKey(commandNode.getUsageText().substring(1)))
                registerCommandPermission(commandNode.getUsageText().substring(1));
        }
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

    public static int fromDefaultPermissionLevel(DefaultPermissionLevel level)
    {
        switch (level)
        {
        case ALL:
            return 0;
        case OP:
            return 4;
        default:
            return 4;
        }

    }
    public static String stripNode(String node) {
        
        int index = node.indexOf("$"); // Find index of $ character
        if (index != -1) { // Check if $ character was found
            node = node.substring(0, index);
        }
        return node;
    }
}
