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

public abstract class ForgeEssentialsCommandBuilder extends CommandProcessing{
	protected LiteralArgumentBuilder<CommandSource> builder;
	boolean enabled;

    // ------------------------------------------------------------
    // Command usage

	public ForgeEssentialsCommandBuilder(boolean enabled) {
		this.builder = Commands.literal(getName()).requires(source -> source.hasPermission(PermissionManager.fromDefaultPermissionLevel(getPermissionLevel())));
		this.enabled = enabled;
	}

	public LiteralArgumentBuilder<CommandSource> getBuilder() {
		return builder;
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

    public List<String> aliases = new ArrayList<>();

    protected final static String PREFIX="fe";

    public List<String> getAliases()
    {
        return aliases;
    }

    /**
     * @deprecated Use {@link #getPrimaryAlias()} instead for downstream classes     *
     */
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

    /**
     * @deprecated Use {@link ForgeEssentialsCommandBuilder#getDefaultSecondaryAliases()} in downstream classes
     * Returns a list of default aliases, that will be added to the configuration on first run
     */
    public String[] getDefaultAliases()
    {
        List<String> list = new ArrayList<>();
        String name = getPrimaryAlias();
        if (!name.startsWith(PREFIX))
        {
            list.add(name);
        }
        list.addAll(Arrays.asList(getDefaultSecondaryAliases()));
        return list.toArray(new String[]{});
    }

    public void setAliases(String[] aliases)
    {
        if (aliases == null)
            setAliases(new ArrayList<String>());
        else
            setAliases(Arrays.asList(aliases));
    }

    public void setAliases(List<String> aliases)
    {
    	this.aliases = aliases;
    }

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

    /**
     * Registers this command and it's permission node
     */
    /*
    public void register()
    {
        if (ServerLifecycleHooks.getCurrentServer() == null)
            return;

        Map<String, ICommand> commandMap = ((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).getCommands();
        if (commandMap.containsKey(getName()))
            LoggingHandler.felog.error(String.format("Command %s registered twice", getName()));

        if (getAliases() != null && !getAliases().isEmpty())
        {
            for (String alias : getAliases())
                if (alias != null && commandMap.containsKey(alias))
                {
                    LoggingHandler.felog.error(String.format("Command alias %s of command %s registered twice", alias, getName()));
                    ICommand old = commandMap.get(alias);
                    LoggingHandler.felog.error(String.format("Old Class: %s has been removed from commandMap!", old.getClass().getCanonicalName()));
                    commandMap.remove(alias);
                }
        }

        ((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(this);
        PermissionManager.registerCommandPermission(this, this.getPermissionNode(), this.getPermissionLevel());
    }

    @SuppressWarnings("unchecked")
    public void deregister()
    {
        if (ServerLifecycleHooks.getCurrentServer() == null)
            return;
        CommandHandler cmdHandler = (CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        Map<String, ICommand> commandMap = cmdHandler.getCommands();
        Set<ICommand> commandSet = cmdHandler.commandSet;

        String commandName = getName();
        List<String> commandAliases = getAliases();
        commandSet.remove(this);
        if (commandName != null)
            commandMap.remove(commandName);
        if (commandAliases != null && !commandAliases.isEmpty())
        {
            for (String alias : commandAliases)
            {
                commandMap.remove(alias);
            }
        }
    }*/
}