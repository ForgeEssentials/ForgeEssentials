package com.forgeessentials.jscripting.command;

import javax.script.ScriptException;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandOptions;
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
         super(true);
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

    /*
     * @Override public void parse() throws CommandException { try { if (arguments.isTabCompletion) { if (options.tabComplete != null) //script.call(options.tabComplete,
     * options.tabComplete, new JsCommandArgs(arguments)); } else { //script.call(options.processCommand, options.processCommand, new JsCommandArgs(arguments)); } } catch
     * (NoSuchMethodException e) { e.printStackTrace(); throw new TranslatedCommandException("Script error: method not found: " + e.getMessage()); } catch (ScriptException e) {
     * e.printStackTrace(); throw new TranslatedCommandException(e.getMessage()); } }
     * 
     * public static void processArguments(CommandParserArgs args) { for (int i = 0; i < args.size(); i++) { Matcher matcher = ARGUMENT_PATTERN.matcher(args.get(i)); if
     * (!matcher.matches()) { continue; }
     * 
     * String modifier = matcher.group(1).toLowerCase(); String rest = matcher.group(2);
     * 
     * ScriptArgument argument = ScriptArguments.get(modifier); if (argument != null) { args.args.set(i, argument.process(args.sender) + rest); } else { try { int idx =
     * Integer.parseInt(modifier); if (idx >= args.size()) throw new SyntaxException("Missing argument @%d", idx); args.args.set(i, args.get(idx) + rest); } catch
     * (NumberFormatException e) { throw new SyntaxException("Unknown argument modifier \"%s\"", modifier); } } } }
     */
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
            script.call(options.processCommand, options.processCommand, ctx.getInput());
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            throw new RuntimeException(e);
        }
        return Command.SINGLE_SUCCESS;
    }
}
