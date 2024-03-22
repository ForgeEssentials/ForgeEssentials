package com.forgeessentials.commands.server;

import java.util.TimerTask;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandDelayedAction extends ForgeEssentialsCommandBuilder
{

    public CommandDelayedAction(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("time", StringArgumentType.string())
                .then(Commands.argument("command", StringArgumentType.greedyString())
                        .executes(CommandContext -> execute(CommandContext, "blank"))));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        long time;
        try
        {
            time = parseTimeReadable(StringArgumentType.getString(ctx, "time"));
        }
        catch (FECommandParsingException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), e.error);
            return Command.SINGLE_SUCCESS;
        }
        final String execute = StringArgumentType.getString(ctx, "command");
        TaskRegistry.schedule(new TimerTask() {
            @Override
            public void run()
            {
                ctx.getSource().getServer().getCommands().performCommand(ctx.getSource(), execute);
            }
        }, time);
        ChatOutputHandler.chatNotification(ctx.getSource(),
                Translator.format(execute, ChatOutputHandler.formatTimeDurationReadableMilli(time, true)));
        return Command.SINGLE_SUCCESS;
    }
}
