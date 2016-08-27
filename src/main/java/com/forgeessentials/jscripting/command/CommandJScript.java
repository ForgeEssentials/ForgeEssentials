package com.forgeessentials.jscripting.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.wrapper.JsCommandSender;
import com.forgeessentials.jscripting.wrapper.JsEntityPlayer;
import com.forgeessentials.util.CommandParserArgs;
import com.google.common.io.PatternFilenameFilter;

public class CommandJScript extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "fejscript";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "jscript" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/jscript <name>: Run a jscript";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleJScripting.PERM + ".run";
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(getCommandUsage(null));
            return;
        }

        // Find existing JS files
        String[] scriptFiles = ModuleJScripting.getCommandsDir().list(new PatternFilenameFilter(".*\\.js"));
        for (int i = 0; i < scriptFiles.length; i++)
            scriptFiles[i] = scriptFiles[i].substring(0, scriptFiles[i].length() - 3);

        // TAB-complete and parse argument
        arguments.tabComplete(scriptFiles);
        String fileName = arguments.remove().toLowerCase();

        if (arguments.isTabCompletion)
            return;

        // Check for file
        File file = new File(ModuleJScripting.getCommandsDir(), fileName + ".js");
        if (!file.exists() || !file.isFile())
            throw new TranslatedCommandException("Script file not found");

        // Initialize engine
        ScriptEngine engine = ModuleJScripting.getEngine();
        if (engine == null)
            throw new TranslatedCommandException("Could not initialize JavaScript engine");
        SimpleBindings scope = new SimpleBindings();
        scope.put("sender", new JsCommandSender(arguments.sender));
        scope.put("player", arguments.senderPlayer == null ? null : new JsEntityPlayer(arguments.senderPlayer));
        scope.put("args", arguments);

        // Run script
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            engine.eval(reader, scope);
        }
        catch (IOException | ScriptException e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException("Error in script: %s", e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException("Error in script: %s", e.getMessage());
        }
    }

}