package com.forgeessentials.backup;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandBackup extends ForgeEssentialsCommandBuilder
{

    public CommandBackup(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "backup";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
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
        return baseBuilder.then(Commands.literal("all").executes(CommandContext -> execute(CommandContext, "all")))
                .then(Commands.literal("dim").then(Commands.argument("dim", DimensionArgument.dimension())
                        .executes(CommandContext -> execute(CommandContext, "dim"))));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("all"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Starting forced backup..."));
            ModuleBackup.backupAll();
            return Command.SINGLE_SUCCESS;
        }
        else if (params.equals("dim"))
        {
            ServerWorld world = DimensionArgument.getDimension(ctx, "dim");
            ModuleBackup.backup(world);
        }
        return Command.SINGLE_SUCCESS;
    }
}
