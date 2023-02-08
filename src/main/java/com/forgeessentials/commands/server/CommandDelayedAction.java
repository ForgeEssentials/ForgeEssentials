package com.forgeessentials.commands.server;

import java.util.TimerTask;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandDelayedAction extends ForgeEssentialsCommandBuilder
{

    public CommandDelayedAction(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "delayedaction";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".delayedaction";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("time", MessageArgument.message())
                        .then(Commands.argument("command", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext)
                                        )
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        long time = parseTimeReadable(MessageArgument.getMessage(ctx, "time").getString());
        final String execute = MessageArgument.getMessage(ctx, "command").getString();
        TaskRegistry.schedule(new TimerTask() {
            @Override
            public void run()
            {
                ctx.getSource().getServer().getCommands().performCommand(ctx.getSource(), execute);
            }
        }, time);
        ChatOutputHandler.chatNotification(ctx.getSource(), Translator.format(execute, ChatOutputHandler.formatTimeDurationReadableMilli(time, true)));
        return Command.SINGLE_SUCCESS;
    }
}
