package com.forgeessentials.backup;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandBackup extends BaseCommand
{

    public CommandBackup(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "backup";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "backup" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.backup.command";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
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
        .then(Commands.literal("all")
                .executes(CommandContext -> execute(CommandContext, "all")
                        )
                )
        .then(Commands.literal("dim")
                .then(Commands.argument("dim", DimensionArgument.dimension())
                        .executes(CommandContext -> execute(CommandContext, "dim")
                                )
                        )
                );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "all")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Starting forced backup..."));
            ModuleBackup.backupAll();
            return Command.SINGLE_SUCCESS;
        }
        else if (params.toString() == "dim")
        {
            ServerWorld world = DimensionArgument.getDimension(ctx, "dim");
            ModuleBackup.backup(world);
        }
        return Command.SINGLE_SUCCESS;
    }
}
