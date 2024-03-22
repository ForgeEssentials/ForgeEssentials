package com.forgeessentials.core.misc;

import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Transition class to the new Permissions API
 */
public class CommandPermissionManager
{
    /*
     * First String is command node Second string is the DefaultPermissionLevel
     */
    protected static Map<String, DefaultPermissionLevel> commandPermissionMap = new WeakHashMap<>();

    /**
     * This method allows you to register permissions for commands
     *
     * @param commandNode
     * @param permissionLevel
     */
    public static void registerCommandPermission(String commandNode, DefaultPermissionLevel permissionLevel)
    {
        commandPermissionMap.put(commandNode, permissionLevel);
        APIRegistry.perms.registerPermission("command." + commandNode, permissionLevel, "");
    }

    /**
     * This method allows you to register permissions for commands
     *
     * @param commandNode
     * @param permissionLevel
     */
    public static void registerCommandPermissionDiscription(String commandNode, DefaultPermissionLevel permissionLevel, String disc)
    {
        commandPermissionMap.put(commandNode, permissionLevel);
        APIRegistry.perms.registerPermission("command." + commandNode, permissionLevel, disc);
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     */
    public static void registerCommandPermissions()
    {
        for (Map.Entry<String, DefaultPermissionLevel> node : getAllUsage().entrySet())
        {
            if (!commandPermissionMap.containsKey(node.getKey()))
            {
                registerCommandPermission(node.getKey(), node.getValue());
                // LoggingHandler.felog.debug("Command: " + org.apache.commons.lang3.StringUtils.rightPad(node.getKey(), 30) + " - Permission: " + node.getValue().name());
            }
            else
            {
                LoggingHandler.felog.debug("Command permission tried to be set twice: " + node.getKey());
            }
        }
    }

    public static int fromDefaultPermissionLevel(DefaultPermissionLevel level)
    {
        switch (level)
        {
        case ALL:
            return 0;
        case OP:
        default:
            return 4;
        }
    }

    public static DefaultPermissionLevel getDefaultCommandPermFromNode(CommandNode<CommandSourceStack> commandNode)
    {
    	try {
            if (commandNode.canUse(new CommandFaker().createCommandSourceStack(0)))
            {
            	return DefaultPermissionLevel.ALL;
            }
            else if (commandNode.canUse(new CommandFaker().createCommandSourceStack(1)) ||
                    commandNode.canUse(new CommandFaker().createCommandSourceStack(2)) ||
                    commandNode.canUse(new CommandFaker().createCommandSourceStack(3)) ||
                    commandNode.canUse(new CommandFaker().createCommandSourceStack(4)))
            {
            	return DefaultPermissionLevel.OP;
            }
            return DefaultPermissionLevel.ALL;
    	}catch(UnsupportedOperationException e) {}
    	return DefaultPermissionLevel.OP;
    }

    /**
     * Strip a commandNode string from beginning to the index of a $ character
     *
     * @param node
     */
    public static String stripNode(String node)
    {

        int index = node.indexOf("$");
        if (index != -1)
        {
            node = node.substring(0, index);
        }
        return node;
    }

    /**
     * Gets all possible command nodes following the given node.
     * @return array of full usage strings under the target node
     */
    public static Map<String, DefaultPermissionLevel> getAllUsage()
    {
        CommandDispatcher<CommandSourceStack> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();
        final TreeMap<String, DefaultPermissionLevel> result = new TreeMap<>();
        getAllUsage(dispatcher.getRoot(), result, "", dispatcher, DefaultPermissionLevel.ALL);
        return result;
    }

    private static void getAllUsage(final CommandNode<CommandSourceStack> node, final Map<String, DefaultPermissionLevel> result, final String prefix,
            CommandDispatcher<CommandSourceStack> dispatcher, DefaultPermissionLevel parentLevel)
    {
        if (node instanceof ArgumentCommandNode && !ModulePermissions.fullcommandNode)
        {
            // LoggingHandler.felog.debug("Found Command Argument: "+ node.getUsageText()+ " For Command: "+ prefix.replace(' ', '.'));
            return;
        }
        if (!prefix.equals(""))
        {
            if (parentLevel == DefaultPermissionLevel.ALL && getDefaultCommandPermFromNode(node) == DefaultPermissionLevel.OP)
            {
                parentLevel = DefaultPermissionLevel.OP;
            }
            if(commandPermissionMap.containsKey(prefix)) {
            	parentLevel = commandPermissionMap.get(prefix);
            }
            result.put(prefix.replace(' ', '.').replace("<", "").replace(">", ""), parentLevel);
        }

        if (node.getRedirect() != null && !prefix.replace(' ', '.').replace("<", "").replace(">", "").startsWith("execute.run.execute"))
        {
            // final String redirect = node.getRedirect() == dispatcher.getRoot() ? "..." : "-> " + node.getRedirect().getUsageText();
            // LoggingHandler.felog.debug("Found Command Redirect: "+ (prefix.isEmpty() ? node.getUsageText() + CommandDispatcher.ARGUMENT_SEPARATOR + redirect : prefix +
            // CommandDispatcher.ARGUMENT_SEPARATOR + redirect));
             if (!node.getRedirect().getChildren().isEmpty())
             {
                 for (final CommandNode<CommandSourceStack> child : node.getRedirect().getChildren())
                 {
                     getAllUsage(child, result, prefix.isEmpty() ? child.getUsageText() : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + child.getUsageText(),
                             dispatcher, parentLevel);
                 }
             }
        }
        else if (!node.getChildren().isEmpty())
        {
            for (final CommandNode<CommandSourceStack> child : node.getChildren())
            {
                getAllUsage(child, result, prefix.isEmpty() ? child.getUsageText() : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + child.getUsageText(),
                        dispatcher, parentLevel);
            }
        }
    }
}
