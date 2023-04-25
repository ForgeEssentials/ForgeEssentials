package com.forgeessentials.client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public abstract class BaseCommand {
	protected LiteralArgumentBuilder<CommandSource> builder;
	boolean enabled;
	String name;
	int permissionLevel;
	
	public BaseCommand(boolean enabled) {
		this.builder = Commands.literal(getPrimaryAlias()).requires(source -> source.hasPermission(getPermissionLevel()));
		this.enabled = enabled;
	}
	
	public LiteralArgumentBuilder<CommandSource> getBuilder() {
		return builder;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return null;
	}
    public String getPrimaryAlias() {
        return "";
    }

    public int getPermissionLevel() {
        return 0;
    }
}
