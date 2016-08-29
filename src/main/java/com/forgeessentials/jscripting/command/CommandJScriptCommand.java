package com.forgeessentials.jscripting.command;

import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.wrapper.JsCommandArgs;
import com.forgeessentials.jscripting.wrapper.JsCommandOptions;
import com.forgeessentials.util.CommandParserArgs;
import com.google.common.base.Preconditions;

public class CommandJScriptCommand extends ParserCommandBase
{

    public final ScriptInstance script;

    private JsCommandOptions options;

    private Object processCommand;

    private Object tabComplete;

    public CommandJScriptCommand(ScriptInstance script, JsCommandOptions options, Object processCommand, Object tabComplete)
    {
        Preconditions.checkNotNull(script);
        Preconditions.checkNotNull(processCommand);
        Preconditions.checkNotNull(options);
        Preconditions.checkNotNull(options.name);
        if (options.usage == null)
            options.usage = "/" + options.name + ": scripted command - no description";
        if (options.permission == null)
            options.permission = ModuleJScripting.PERM + ".command." + options.name;
        this.script = script;
        this.options = options;
        this.processCommand = processCommand;
        this.tabComplete = tabComplete;
    }

    @Override
    public String getCommandName()
    {
        return options.name;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return options.usage;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return options.opOnly ? PermissionLevel.OP : PermissionLevel.TRUE;
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
                if (tabComplete != null)
                    script.getInvocable().invokeMethod(tabComplete, "call", processCommand, new JsCommandArgs(arguments));
            }
            else
            {
                script.getInvocable().invokeMethod(processCommand, "call", processCommand, new JsCommandArgs(arguments));
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new TranslatedCommandException("Script error: method not found");
        }
        catch (ScriptException e)
        {
            throw new TranslatedCommandException("Script error: " + e.getMessage());
        }
    }

}