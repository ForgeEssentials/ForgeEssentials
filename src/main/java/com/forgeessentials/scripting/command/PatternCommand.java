package com.forgeessentials.scripting.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.scripting.ModuleScripting;
import com.forgeessentials.scripting.ScriptParser;
import com.forgeessentials.scripting.ScriptParser.MissingPermissionException;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.SyntaxException;
import com.forgeessentials.scripting.pattern.Pattern;
import com.forgeessentials.scripting.pattern.Pattern.PatternMatchException;
import com.forgeessentials.scripting.pattern.PatternParser;
import com.forgeessentials.scripting.pattern.PatternParser.ParseResult;
import com.google.gson.annotations.Expose;

public class PatternCommand extends ForgeEssentialsCommandBase implements Loadable
{

    public static Map<String, PatternCommand> patternCommands = new HashMap<>();

    public static class CommandPattern extends Pattern
    {

        private List<String> script;

        public CommandPattern(String pattern, List<String> script)
        {
            super(pattern);
            this.script = script;
        }

    }

    protected String name;

    protected String usage;

    protected String permission;

    protected Map<String, PermissionLevel> extraPermissions = new HashMap<>();

    protected PermissionLevel permissionLevel = PermissionLevel.TRUE;

    protected Map<String, List<String>> patterns = new HashMap<>();

    @Expose(serialize = false)
    protected PatternParser<CommandPattern> parser;

    public PatternCommand(String name, String usage, String permission)
    {
        this.name = name;
        this.usage = usage;
        this.permission = permission;
        patternCommands.put(name, this);
        register();
    }

    @Override
    public void registerExtraPermissions()
    {
        for (Entry<String, PermissionLevel> perm : extraPermissions.entrySet())
            APIRegistry.perms.registerPermission(perm.getKey(), perm.getValue(), String.format("Permission for Pattern command /%s", name));
    }

    @Override
    public void afterLoad()
    {
        if (extraPermissions == null)
            extraPermissions = new HashMap<>();
        patternCommands.put(name, this);
        register();
    }

    public static void loadAll()
    {
        patternCommands = DataManager.loadAll(PatternCommand.class, ModuleScripting.commandsDir);
    }

    public static void saveAll()
    {
        DataManager.saveAll(patternCommands, ModuleScripting.commandsDir);
    }

    public static void deregisterAll()
    {
        for (PatternCommand pattern : patternCommands.values())
        {
            pattern.deregister();
        }
    }

    public Map<String, List<String>> getPatterns()
    {
        parser = null;
        return patterns;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        processCommand(sender, StringUtils.join(args, " "));
    }

    public void processCommand(ICommandSender sender, String cmd) throws CommandException
    {
        if (parser == null)
        {
            try
            {
                parser = new PatternParser<>();
                for (Entry<String, List<String>> pattern : patterns.entrySet())
                    parser.add(new CommandPattern(pattern.getKey(), pattern.getValue()));
            }
            catch (IllegalArgumentException e)
            {
                throw new TranslatedCommandException("Error in shortcut command config: %s", e.getMessage());
            }
        }
        try
        {
            ParseResult<CommandPattern> result = parser.parse(cmd, sender);
            ScriptParser.run(result.pattern.script, sender, result.arguments);
        }
        catch (MissingPlayerException e)
        {
            throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND, name);
        }
        catch (MissingPermissionException e)
        {
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        }
        catch (SyntaxException e)
        {
            throw new TranslatedCommandException("Error in script \"%s\": %s", name, e.getMessage());
        }
        catch (ScriptException e)
        {
            throw new CommandException(e.getMessage());
        }
        catch (PatternMatchException e)
        {
            throw new CommandException(e.getMessage());
        }
    }

    @Override
    public String getCommandName()
    {
        return name;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return usage;
    }

    @Override
    public String getPermissionNode()
    {
        return permission;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return permissionLevel;
    }

    public Map<String, PermissionLevel> getExtraPermissions()
    {
        return extraPermissions;
    }

}
