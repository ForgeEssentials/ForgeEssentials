package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.commandperms.PermissionManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public abstract class ForgeEssentialsCommandBuilder extends CommandProcessor {
	protected LiteralArgumentBuilder<CommandSource> baseBuilder;

	boolean enabled;

	// ------------------------------------------------------------
	// Command usage

	public ForgeEssentialsCommandBuilder(boolean enabled) {
		this.baseBuilder = Commands.literal(getName()).requires(
				source -> source.hasPermission(PermissionManager.fromDefaultPermissionLevel(getPermissionLevel())));
		this.enabled = enabled;

	}

	public LiteralArgumentBuilder<CommandSource> getMainBuilder() {
		return baseBuilder;
	}

	public boolean isEnabled() {
		return enabled;
	}

	abstract public LiteralArgumentBuilder<CommandSource> setExecution();

	// ------------------------------------------------------------
	// Permissions
	@Deprecated
	public boolean checkPermission(CommandSource sender) {
		if (!canConsoleUseCommand() && !(sender.getEntity() instanceof PlayerEntity))
			return false;
		return this.checkCommandPermission(sender);
	}

	public abstract boolean canConsoleUseCommand();

	/**
	 * @deprecated Check, if the sender has permissions to use this command
	 */
	public boolean checkCommandPermission(CommandSource sender) {
		ICommandSource source = GetSource(sender);
		if (getPermissionNode() == null || getPermissionNode().isEmpty())
			return true;
		if (source instanceof MinecraftServer || source instanceof CommandBlockLogic)
			return true;
		return APIRegistry.perms.checkPermission(
				UserIdent.get(((PlayerEntity) sender.getEntity()).getGameProfile()).getPlayer(), getPermissionNode());
	}

	/**
	 * formerly of PermissionObject
	 */
	public abstract String getPermissionNode();

	public abstract DefaultPermissionLevel getPermissionLevel();

	// ------------------------------------------------------------
	// Command alias

	@Nonnull
	protected abstract String getPrimaryAlias();

	@Nonnull
	protected String[] getDefaultSecondaryAliases() {
		return new String[] {};
	}

	private List<String> Aliases;

	protected final static String PREFIX = "fe";

	public String getName() {
		String name = getPrimaryAlias();
		if (name.startsWith(PREFIX)) {
			return name;
		} else {
			if (name.startsWith("/")) {
				String newname = name.substring(1);
				if (newname.startsWith(PREFIX)) {
					return name;
				} else {
					return "/" + PREFIX + newname;
				}
			} else {
				return PREFIX + name;
			}
		}
	}

	public List<String> getAliases() {
		Aliases = new ArrayList<>(Arrays.asList(getDefaultSecondaryAliases()));
		return Aliases;
	}

	public List<String> getDefaultAliases() {
		List<String> list = getAliases();
		String name = getPrimaryAlias();
		if (!name.startsWith(PREFIX)) {
			list.add(name);
		}
		return list;
	}

	public void setAliases(List<String> aliases) {
		Aliases = aliases;
	}

	/**
	 * Registers additional permissions
	 */
	public void registerExtraPermissions() {
		/* do nothing */
	}

}