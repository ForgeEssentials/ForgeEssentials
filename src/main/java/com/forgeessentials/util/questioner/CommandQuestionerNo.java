package com.forgeessentials.util.questioner;

import com.forgeessentials.core.commands.BaseCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandQuestionerNo extends BaseCommand
{
    public CommandQuestionerNo(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    private final boolean type = false;

    @Override
    public String getPrimaryAlias()
    {
        return "no";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "decline", "deny" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.questioner";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .executes(CommandContext -> execute(CommandContext)
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        Questioner.answer(ctx.getSource(), type);
        return Command.SINGLE_SUCCESS;
    }
}
