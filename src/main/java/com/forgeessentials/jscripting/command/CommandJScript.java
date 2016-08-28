package com.forgeessentials.jscripting.command;

import java.io.IOException;

import javax.script.ScriptException;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
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
        String scriptName = arguments.remove();

        try
        {
            ScriptInstance script = ModuleJScripting.getScript(ModuleJScripting.COMMANDS_DIR + scriptName + ".js");
            if (script == null)
                throw new TranslatedCommandException("Script not found");
            script.runCommand(arguments);
        }
        catch (IOException e1)
        {
            throw new TranslatedCommandException("Error loading script file");
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException("Error compiling script: %s", e.getMessage());
        }
    }

}