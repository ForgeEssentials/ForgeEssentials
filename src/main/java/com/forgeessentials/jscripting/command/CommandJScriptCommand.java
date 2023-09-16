package com.forgeessentials.jscripting.command;

import javax.script.ScriptException;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandArgs;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandOptions;
import com.forgeessentials.util.CommandContextParcer;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandJScriptCommand extends ForgeEssentialsCommandBuilder
{

    public final ScriptInstance script;

    private JsCommandOptions options;

    // private static final Pattern ARGUMENT_PATTERN =
    // Pattern.compile("@(\\w+)(.*)");

     public CommandJScriptCommand(ScriptInstance script, JsCommandOptions options) {
         super(true, options.name, options.opOnly ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL);
         Preconditions.checkNotNull(script);
         Preconditions.checkNotNull(options);
         Preconditions.checkNotNull(options.name);
         Preconditions.checkNotNull(options.processCommand);
         if (options.usage == null)
         {
             options.usage = "/" + options.name + ": scripted command - no description";
         }
         this.script = script;
        this.options = options;
     }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return options.name;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return options.opOnly ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.executes(context -> execute(context, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        try
        {
        	script.call(options.processCommand, options.processCommand, new JsCommandArgs(new CommandContextParcer(ctx, params)));
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            ChatOutputHandler.chatError(ctx.getSource(), "Script error: method not found: " + e.getMessage());
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
        }
        catch (FECommandParsingException e) {
        	e.printStackTrace();
			ChatOutputHandler.chatError(ctx.getSource(), e.error);
		}
        return Command.SINGLE_SUCCESS;
    }
}
