package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.misc.commandTools.PermissionManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public abstract class ForgeEssentialsCommandBuilder extends CommandProcessor
{
    protected LiteralArgumentBuilder<CommandSource> baseBuilder;

    boolean enabled;

    // ------------------------------------------------------------
    // Command usage

    public ForgeEssentialsCommandBuilder(boolean enabled)
    {
        this.baseBuilder = Commands.literal(getName()).requires(
                source -> source.hasPermission(PermissionManager.fromDefaultPermissionLevel(getPermissionLevel())));
        this.enabled = enabled;

    }

    public LiteralArgumentBuilder<CommandSource> getMainBuilder()
    {
        return baseBuilder;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    abstract public LiteralArgumentBuilder<CommandSource> setExecution();

    // ------------------------------------------------------------
    // Permissions
    public boolean hasPermission(CommandSource sender, String perm)
    {
        if (!canConsoleUseCommand() && !(sender.getEntity() instanceof PlayerEntity))
            return false;
        if (sender.getEntity() != null && sender.getEntity() instanceof PlayerEntity)
            return APIRegistry.perms.checkPermission(getServerPlayer(sender), perm);
        ICommandSource source = GetSource(sender);
        return source instanceof MinecraftServer || source instanceof CommandBlockLogic;
    }

    public abstract boolean canConsoleUseCommand();

    public abstract DefaultPermissionLevel getPermissionLevel();

    // ------------------------------------------------------------
    // Command alias

    @Nonnull
    protected abstract String getPrimaryAlias();

    @Nonnull
    protected String[] getDefaultSecondaryAliases()
    {
        return new String[] {};
    }

    private List<String> Aliases;

    protected final static String PREFIX = "fe";

    public String getName()
    {
        String name = getPrimaryAlias();
        if (name.startsWith(PREFIX))
        {
            return name;
        }
        else
        {
            if (name.startsWith("/"))
            {
                String newname = name.substring(1);
                if (newname.startsWith(PREFIX))
                {
                    return name;
                }
                else
                {
                    return "/" + PREFIX + newname;
                }
            }
            else
            {
                return PREFIX + name;
            }
        }
    }

    public List<String> getAliases()
    {
        Aliases = new ArrayList<>(Arrays.asList(getDefaultSecondaryAliases()));
        return Aliases;
    }

    public List<String> getDefaultAliases()
    {
        List<String> list = getAliases();
        String name = getPrimaryAlias();
        if (!name.startsWith(PREFIX))
        {
            list.add(name);
        }
        return list;
    }

    public void setAliases(List<String> aliases)
    {
        Aliases = aliases;
    }

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

}