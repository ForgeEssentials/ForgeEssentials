package com.forgeessentials.core.misc.commandperms;

import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Transition class to the new Permissions API
 */
public class PermissionManager {
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
	public static void registerCommandPermission(String commandNode, DefaultPermissionLevel permissionLevel) {
		commandPermissionMap.put(commandNode, permissionLevel);
		registerCommandPermissionDiscription(commandNode, permissionLevel, "");
	}

	/**
	 * This method allows you to register permissions for commands
	 *
	 * @param commandNode
	 * @param permissionLevel
	 */
	public static void registerCommandPermissionDiscription(String commandNode, DefaultPermissionLevel permissionLevel, String disc) {
		commandPermissionMap.put(commandNode, permissionLevel);
		APIRegistry.perms.registerPermission("command."+commandNode, permissionLevel, disc);
	}

	/**
	 * <b>FOR INTERNAL USE ONLY</b> <br>
	 */
	public static void registerCommandPermissions() {
		for(Map.Entry<String, DefaultPermissionLevel> node : getAllUsage().entrySet()) {
			if (!commandPermissionMap.containsKey(node.getKey())) {
				registerCommandPermission(node.getKey(), node.getValue());
				LoggingHandler.felog.debug("Command: " + StringUtils.rightPad(node.getKey(), 30) + " - Permission: " + node.getValue().name());
			}
			else {
				LoggingHandler.felog.debug("Command Tried to be registered twice: " + node.getKey());
			}
		}
	}

	public static int fromDefaultPermissionLevel(DefaultPermissionLevel level) {
		switch (level) {
			case ALL :
				return 0;
			case OP :
				return 4;
			default :
				return 4;
		}
	}

	public static DefaultPermissionLevel getCommandPermFromNode(CommandNode<CommandSource> commandNode) {
		DefaultPermissionLevel result;
		if(commandNode.canUse(new CommandFaker().createCommandSourceStack(0))) {
			result = DefaultPermissionLevel.ALL;
		}
		else if(commandNode.canUse(new CommandFaker().createCommandSourceStack(1))||
				commandNode.canUse(new CommandFaker().createCommandSourceStack(2))||
				commandNode.canUse(new CommandFaker().createCommandSourceStack(3))||
				commandNode.canUse(new CommandFaker().createCommandSourceStack(4))){
			result = DefaultPermissionLevel.OP;
		}
		else {
			result = DefaultPermissionLevel.ALL;
		}
		return result;
	}

	/**
	 * Strip a commandNode string from beginning to the index of a $ character
	 *
	 * @param commandNode
	 */
	public static String stripNode(String node) {

		int index = node.indexOf("$");
		if (index != -1) {
			node = node.substring(0, index);
		}
		return node;
	}

    /**
     * Gets all possible command nodes following the given node.
     * 
     * @param node target node to get child usage strings for
     * @param source a custom "source" object, usually representing the originator of this command
     * @return array of full usage strings under the target node
     */
    public static Map<String, DefaultPermissionLevel> getAllUsage() {
		CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();
        final TreeMap<String, DefaultPermissionLevel> result = new TreeMap<String, DefaultPermissionLevel>();
        getAllUsage(dispatcher.getRoot(), result, "", dispatcher, DefaultPermissionLevel.ALL);
        return result;
    }

    private static void getAllUsage(final CommandNode<CommandSource> node, final Map<String, DefaultPermissionLevel> result, final String prefix, CommandDispatcher<CommandSource> dispatcher, DefaultPermissionLevel parentLevel) {
    	if (node instanceof ArgumentCommandNode && !ModulePermissions.fullcommandNode) {
			LoggingHandler.felog.debug("Found Command Argument: "+ node.getUsageText()+ " For Command: "+ prefix.replace(' ', '.'));
			return;
        }
        if (prefix != "") {
        	if(parentLevel == DefaultPermissionLevel.ALL && getCommandPermFromNode(node) == DefaultPermissionLevel.OP) {
        		parentLevel=DefaultPermissionLevel.OP;
        	}
            result.put(prefix.replace(' ', '.'), parentLevel);
        }

        if (node.getRedirect() != null) {
            final String redirect = node.getRedirect() == dispatcher.getRoot() ? "..." : "-> " + node.getRedirect().getUsageText();
			LoggingHandler.felog.debug("Found Command Redirect: "+ (prefix.isEmpty() ? node.getUsageText() + CommandDispatcher.ARGUMENT_SEPARATOR + redirect : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + redirect));
            //result.add(prefix.isEmpty() ? node.getUsageText() + CommandDispatcher.ARGUMENT_SEPARATOR + redirect : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + redirect);
        } else if (!node.getChildren().isEmpty()) {
            for (final CommandNode<CommandSource> child : node.getChildren()) {
                getAllUsage(child, result, prefix.isEmpty() ? child.getUsageText() : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + child.getUsageText(), dispatcher, parentLevel);
            }
        }
    }
}
