package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.PermissionManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class ForgeEssentialsCommandBuilder extends CommandProcessor{
	protected LiteralArgumentBuilder<CommandSource> baseBuilder;
	protected List<LiteralArgumentBuilder<CommandSource>> builders = new ArrayList<LiteralArgumentBuilder<CommandSource>>();

    boolean enabled;

    // ------------------------------------------------------------
    // Command usage

    public ForgeEssentialsCommandBuilder(boolean enabled) {
        if(!getAliases().isEmpty()) {
	       for(String alias : getAliases()) {
	            LiteralArgumentBuilder<CommandSource> build = baseBuilder;
	            build = Commands.literal(alias).requires(source -> source.hasPermission(PermissionManager.fromDefaultPermissionLevel(getPermissionLevel())));
	            builders.add(build);
	        }
        }
		this.baseBuilder = Commands.literal(getName()).requires(source -> source.hasPermission(PermissionManager.fromDefaultPermissionLevel(getPermissionLevel())));
		this.enabled = enabled;
		
	}

	public LiteralArgumentBuilder<CommandSource> getMainBuilder() {
		return baseBuilder;
	}

	public List<LiteralArgumentBuilder<CommandSource>> getBuilders()
    {
        return builders;
    }

	public boolean isEnabled() {
		return enabled;
	}

	abstract public LiteralArgumentBuilder<CommandSource> setExecution();

    // ------------------------------------------------------------
    // Permissions

    public boolean checkPermission(MinecraftServer server, CommandSource sender)
    {
        if (!canConsoleUseCommand() && !(sender.getEntity() instanceof PlayerEntity))
            return false;
        return this.checkCommandPermission(sender);
    }

    public abstract boolean canConsoleUseCommand();

	/**
     * Check, if the sender has permissions to use this command
     */
    public boolean checkCommandPermission(CommandSource sender)
    {
    	ICommandSource source = GetSource(sender);
        if (getPermissionNode() == null || getPermissionNode().isEmpty())
            return true;
        if (source instanceof MinecraftServer || source instanceof CommandBlockLogic)
            return true;
        return PermissionAPI.hasPermission(UserIdent.get(((PlayerEntity) sender.getEntity()).getGameProfile()).getPlayer(),getPermissionNode());
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

    protected final static String PREFIX="fe";

    public String getName() {
        String name = getPrimaryAlias();
        if (name.startsWith(PREFIX)) {
            return name;
        } else
        {
            if (name.startsWith("/"))
            {
                   String newname = name.substring(1);
                if (newname.startsWith(PREFIX)) {
                    return name;
                } else {
                    return "/" + PREFIX + newname;
                }
            } else
            {
                return PREFIX + name;
            }
        }
    }

    public List<String> getAliases()
    {
        return Arrays.asList(getDefaultSecondaryAliases());
    }

    public List<String> getDefaultAliases()
    {
        List<String> list = new ArrayList<>();
        String name = getPrimaryAlias();
        if (!name.startsWith(PREFIX))
        {
            list.add(name);
        }
        list.addAll(Arrays.asList(getDefaultSecondaryAliases()));
        return list;
    }

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

}