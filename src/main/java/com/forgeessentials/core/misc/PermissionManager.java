package com.forgeessentials.core.misc;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
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
					LoggingHandler.felog.debug("Found Permission for command: {" + fromDefaultPermissionLevel(level)
							+ "} " + commandNode.getUsageText());
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
	 * TODO This method should be removed in the PR
	 *
	 * @param command a command
	 */
	@Deprecated
	public static void registerCommandPermissions() {
		CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands()
				.getDispatcher();

		for (CommandNode<CommandSource> commandNode : dispatcher.getRoot().getChildren()) {
			if (commandNode.getRequirement() != null) {
				LoggingHandler.felog.debug("Found command: " + StringUtils.rightPad(commandNode.getUsageText(), 20)
						+ " - Permission: " + getCommandPermission(commandNode.getUsageText()));
			} else {
				LoggingHandler.felog.info("Bad CommandRe: " + StringUtils.rightPad(commandNode.getUsageText(), 20));
			}
			if (!commandPermissions.containsKey(commandNode.getUsageText()))
				registerCommandPermission(commandNode.getUsageText());
		}
	}

	@Deprecated
	public static DefaultPermissionLevel fromIntegerLevel(int value) {
		switch (value) {
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

	@Deprecated
	public static int fromDefaultPermissionLevel(DefaultPermissionLevel level) {
		switch (level) {
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
