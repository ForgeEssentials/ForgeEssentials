package com.forgeessentials.client.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public abstract class BaseCommand
{
    protected LiteralArgumentBuilder<CommandSourceStack> builder;
    boolean enabled;
    String name;
    int permissionLevel;

    public BaseCommand(boolean enabled)
    {
        this.builder = Commands.literal(getPrimaryAlias())
                .requires(source -> source.hasPermission(getPermissionLevel()));
        this.enabled = enabled;
    }

    public LiteralArgumentBuilder<CommandSourceStack> getBuilder()
    {
        return builder;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return null;
    }

    public String getPrimaryAlias()
    {
        return "";
    }

    public int getPermissionLevel()
    {
        return 0;
    }
}
