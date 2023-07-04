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
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

/**
 * Transition class to the new Permissions API
 */
public class PermissionManager {
	/*
	 * First String is command name Second string is the node
	 */
	protected static Map<String, String> commandPermissions = new WeakHashMap<>();
	protected static Map<String, DefaultPermissionLevel> commandPermissionMap = new WeakHashMap<>();

	@Deprecated
	public static String getCommandPermission(String commandName) {
		String permission = commandPermissions.get(commandName);
		if (permission != null)
			return permission;
		return "command." + commandName;
	}

	@Deprecated
	public static DefaultPermissionLevel getCommandLevel(String commandName) {
		CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands()
				.getDispatcher();

		for (CommandNode<CommandSource> commandNode : dispatcher.getRoot().getChildren()) {
			if (commandNode.getRequirement() != null) {
				String permission = stripNode(commandNode.getRequirement().toString());
				// LoggingHandler.felog.info("Compairing: " + commandNode.getUsageText() + "
				// with: " + commandNode.getUsageText());
				if (commandName == commandNode.getUsageText()) {
					DefaultPermissionLevel level = DefaultPermissionHandler.INSTANCE
							.getDefaultPermissionLevel(permission);
					//LoggingHandler.felog.debug("Found Permission for command: {" + fromDefaultPermissionLevel(level)
					//		+ "} " + commandNode.getUsageText());
					return level;
				}
			}
		}
		LoggingHandler.felog.info("Failed to get Permission for command: " + commandName);
		return DefaultPermissionLevel.OP;
	}

	/**
	 * <b>FOR INTERNAL USE ONLY</b> <br>
	 * This method should not be called directly, but instead is called by forge
	 * upon registration of a new command
	 *
	 * @param command
	 */
	@Deprecated
	public static void registerCommandPermission(String commandName) {
		commandPermissions.put(commandName, getCommandPermission(commandName));
		APIRegistry.perms.registerNode(getCommandPermission(commandName), getCommandLevel(commandName), "");
	}

	/**
	 * This method allows you to register permissions for commands that cannot
	 * implement the PermissionObject interface for any reason.
	 *
	 * @param command
	 * @param permission
	 * @param permissionLevel
	 */
	@Deprecated
	public static void registerCommandPermission(String commandName, String permission,
			DefaultPermissionLevel permissionLevel) {
		commandPermissions.put(commandName, permission);
		APIRegistry.perms.registerNode(permission, permissionLevel, "");
	}

	/**
	 * This method allows you to register permissions for commands that cannot
	 * implement the PermissionObject interface for any reason.
	 *
	 * @param command
	 * @param permission
	 */
	@Deprecated
	public static void registerCommandPermission(String commandName, String permission) {
		registerCommandPermission(commandName, permission, getCommandLevel(commandName));
	}

	/**
	 * <b>FOR INTERNAL USE ONLY</b> <br>
	 */
	public static void registerCommandPermissions() {
		CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands().getDispatcher();

		for(Map.Entry<String, DefaultPermissionLevel> node : getAllUsage().entrySet()) {
			LoggingHandler.felog.debug("Command: " + StringUtils.rightPad(node.getKey(), 30) + " - Permission: " + node.getValue().name());
		}
		for (CommandNode<CommandSource> commandNode : dispatcher.getRoot().getChildren()) {
			if (commandNode.getRequirement() != null) {
				//LoggingHandler.felog.debug("Found command: " + StringUtils.rightPad(commandNode.getUsageText(), 20)
				//		+ " - Permission: " + getCommandPermission(commandNode.getUsageText()));
			} else {
				LoggingHandler.felog.info("Bad CommandRe: " + StringUtils.rightPad(commandNode.getUsageText(), 20));
			}
			if (!commandPermissions.containsKey(commandNode.getUsageText()))
				registerCommandPermission(commandNode.getUsageText());
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

	public static DefaultPermissionLevel getCommandPermFromNode(CommandNode<CommandSource> commandNode, String name) {
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
			result = DefaultPermissionLevel.NONE;
		}
		if(name != null) {
			LoggingHandler.felog.debug("Command: "+name+" Requires: "+result.name());
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
        getAllUsage(dispatcher.getRoot(), result, "", dispatcher);
        return result;
    }

    private static void getAllUsage(final CommandNode<CommandSource> node, final Map<String, DefaultPermissionLevel> result, final String prefix, CommandDispatcher<CommandSource> dispatcher) {
    	if (node instanceof ArgumentCommandNode && ModulePermissions.fullcommandNode) {
			LoggingHandler.felog.debug("Found Command Argument: "+ node.getUsageText()+ " For Command: "+ prefix);
			return;
        }
        if (node.getCommand() != null) {
            result.put(prefix, getCommandPermFromNode(node, prefix));
        }

        if (node.getRedirect() != null) {
            final String redirect = node.getRedirect() == dispatcher.getRoot() ? "..." : "-> " + node.getRedirect().getUsageText();
			LoggingHandler.felog.debug("Found Command Redirect: "+ (prefix.isEmpty() ? node.getUsageText() + CommandDispatcher.ARGUMENT_SEPARATOR + redirect : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + redirect));
            //result.add(prefix.isEmpty() ? node.getUsageText() + CommandDispatcher.ARGUMENT_SEPARATOR + redirect : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + redirect);
        } else if (!node.getChildren().isEmpty()) {
            for (final CommandNode<CommandSource> child : node.getChildren()) {
                getAllUsage(child, result, prefix.isEmpty() ? child.getUsageText() : prefix + CommandDispatcher.ARGUMENT_SEPARATOR + child.getUsageText(), dispatcher);
            }
        }
    }
}
