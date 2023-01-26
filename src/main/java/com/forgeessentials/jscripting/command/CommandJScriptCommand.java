package com.forgeessentials.jscripting.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandArgs;
import com.forgeessentials.jscripting.fewrapper.fe.JsCommandOptions;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.util.CommandParserArgs;
import com.google.common.base.Preconditions;

public class CommandJScriptCommand extends BaseCommand
{

    public final ScriptInstance script;

    private JsCommandOptions options;

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("@(\\w+)(.*)");

    public CommandJScriptCommand(ScriptInstance script, JsCommandOptions options)
    {
        Preconditions.checkNotNull(script);
        Preconditions.checkNotNull(options);
        Preconditions.checkNotNull(options.name);
        Preconditions.checkNotNull(options.processCommand);
        if (options.usage == null)
            options.usage = "/" + options.name + ": scripted command - no description";
        if (options.permission == null)
            options.permission = ModuleJScripting.PERM + ".command." + options.name;
        this.script = script;
        this.options = options;
    }

    @Override
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return options.permission;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        try
        {
            if (arguments.isTabCompletion)
            {
                if (options.tabComplete != null)
                    script.call(options.tabComplete, options.tabComplete, new JsCommandArgs(arguments));
            }
            else
            {
                script.call(options.processCommand, options.processCommand, new JsCommandArgs(arguments));
            }
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException("Script error: method not found: " + e.getMessage());
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException(e.getMessage());
        }
    }

    public static void processArguments(CommandParserArgs args)
    {
        for (int i = 0; i < args.size(); i++)
        {
            Matcher matcher = ARGUMENT_PATTERN.matcher(args.get(i));
            if (!matcher.matches())
            {
                continue;
            }

            String modifier = matcher.group(1).toLowerCase();
            String rest = matcher.group(2);

            ScriptArgument argument = ScriptArguments.get(modifier);
            if (argument != null)
            {
                args.args.set(i, argument.process(args.sender) + rest);
            }
            else
            {
                try
                {
                    int idx = Integer.parseInt(modifier);
                    if (idx >= args.size())
                        throw new SyntaxException("Missing argument @%d", idx);
                    args.args.set(i, args.get(idx) + rest);
                }
                catch (NumberFormatException e)
                {
                    throw new SyntaxException("Unknown argument modifier \"%s\"", modifier);
                }
            }
        }
    }
}
