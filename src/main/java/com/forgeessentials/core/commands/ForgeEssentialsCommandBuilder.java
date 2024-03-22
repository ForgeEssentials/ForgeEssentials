package com.forgeessentials.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.misc.CommandPermissionManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public abstract class ForgeEssentialsCommandBuilder extends CommandProcessor
{
    protected LiteralArgumentBuilder<CommandSourceStack> baseBuilder;

    boolean enabled;

    // ------------------------------------------------------------
    // Command usage

    public ForgeEssentialsCommandBuilder(boolean enabled)
    {
        this.baseBuilder = Commands.literal(getName()).requires(
                source -> source.hasPermission(CommandPermissionManager.fromDefaultPermissionLevel(getPermissionLevel())));
        this.enabled = enabled;

    }

    public ForgeEssentialsCommandBuilder(boolean enabled, String name, DefaultPermissionLevel level)
    {
        this.baseBuilder = Commands.literal(getFullName(name)).requires(
                source -> source.hasPermission(CommandPermissionManager.fromDefaultPermissionLevel(level)));
        this.enabled = enabled;

    }

    public LiteralArgumentBuilder<CommandSourceStack> getMainBuilder()
    {
        return baseBuilder;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    abstract public LiteralArgumentBuilder<CommandSourceStack> setExecution();

    // ------------------------------------------------------------
    // Permissions
    public boolean hasPermission(CommandSourceStack sender, String perm)
    {
        if (!canConsoleUseCommand() && !(sender.getEntity() instanceof Player))
            return false;
        if (sender.getEntity() != null && sender.getEntity() instanceof Player)
            return APIRegistry.perms.checkPermission(getServerPlayer(sender), perm);
        CommandSource source = GetSource(sender);
        return source instanceof MinecraftServer || source instanceof BaseCommandBlock;
    }

    public abstract boolean canConsoleUseCommand();

    public abstract DefaultPermissionLevel getPermissionLevel();

    // ------------------------------------------------------------
    // Command alias

    protected @NotNull abstract String getPrimaryAlias();

    @Nonnull
    protected String[] getDefaultSecondaryAliases()
    {
        return new String[] {};
    }

    protected final static String PREFIX = "fe";

    public String getName()
    {
    	return getFullName(getPrimaryAlias());
    }

    public String getFullName(String name) {
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
        return new ArrayList<>(Arrays.asList(getDefaultSecondaryAliases()));
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

    /**
     * Registers additional permissions
     */
    public void registerExtraPermissions()
    {
        /* do nothing */
    }

}