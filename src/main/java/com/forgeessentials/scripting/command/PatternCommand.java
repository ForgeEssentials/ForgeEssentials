package com.forgeessentials.scripting.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.forgeessentials.scripting.ScriptParser;
import com.forgeessentials.scripting.ScriptParser.MissingArgumentException;
import com.forgeessentials.scripting.ScriptParser.MissingPermissionException;
import com.forgeessentials.scripting.ScriptParser.MissingPlayerException;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.google.gson.annotations.Expose;

public class PatternCommand extends ForgeEssentialsCommandBase implements Loadable {

    public static Map<String, PatternCommand> patternCommands = new HashMap<>();

    protected String name;

    protected String usage;

    protected String permission;

    protected RegisteredPermValue permissionLevel = RegisteredPermValue.TRUE;

    protected Map<String, List<String>> patterns = new HashMap<>();

    @Expose(serialize = false)
    protected boolean error = false;

    @Expose(serialize = false)
    protected List<PatternData> patternCache;

    protected enum ArgumentType {
        NONE, PLAYER, GROUP, ZONE, DECIMAL, FLOAT;
    }

    protected static class PatternData {

        public Pattern pattern;

        public List<ArgumentType> argumentTypes = new ArrayList<>();

        public List<String> script;

        public PatternData(List<String> script)
        {
            this.script = script;
        }

    }

    public PatternCommand(String name, String usage, String permission)
    {
        this.name = name;
        this.usage = usage;
        this.permission = permission;
        patternCommands.put(name, this);
        register();
    }

    @Override
    public void afterLoad()
    {
        patternCommands.put(name, this);
        register();
    }

    public static void loadAll()
    {
        patternCommands = DataManager.getInstance().loadAll(PatternCommand.class);
    }

    public static void saveAll()
    {
        DataManager.getInstance().saveAll(patternCommands);
    }

    public Map<String, List<String>> getPatterns()
    {
        error = false;
        patternCache = null;
        return patterns;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        processCommand(sender, StringUtils.join(args, " "));
    }

    public void initializePatterns()
    {
        patternCache = new ArrayList<>();
        for (Entry<String, List<String>> pattern : patterns.entrySet())
        {
            PatternData data = new PatternData(pattern.getValue());
            StringBuilder regex = new StringBuilder();
            for (String part : pattern.getKey().split(" "))
            {
                if (part.isEmpty())
                    continue;
                if (part.charAt(0) == '@')
                {
                    switch (part.substring(1))
                    {
                    case "f":
                        regex.append("([+-]?\\d+(?:\\.\\d+)?) ");
                        data.argumentTypes.add(ArgumentType.FLOAT);
                        break;
                    case "d":
                        regex.append("([+-]?\\d+) ");
                        data.argumentTypes.add(ArgumentType.DECIMAL);
                        break;
                    case "p":
                    case "player":
                        regex.append("(\\S+) ");
                        data.argumentTypes.add(ArgumentType.PLAYER);
                        break;
                    case "g":
                    case "group":
                        regex.append("(\\S+) ");
                        data.argumentTypes.add(ArgumentType.GROUP);
                        break;
                    case "zone":
                        regex.append("(\\d+) ");
                        data.argumentTypes.add(ArgumentType.ZONE);
                        break;
                    case "":
                        regex.append("(\\S+) ");
                        data.argumentTypes.add(ArgumentType.NONE);
                        break;
                    default:
                        regex.append("(\\S+) ");
                        data.argumentTypes.add(ArgumentType.NONE);
                        error = true;
                        OutputHandler.felog.severe(Translator.format("Unknown argument type %s in pattern \"%s\" of shortcut command %s", //
                                part.substring(1), pattern.getKey(), name));
                        break;
                    }
                }
                else
                {
                    regex.append(part);
                    regex.append(' ');
                }
            }
            // Cut off final space
            if (regex.length() > 0 && regex.charAt(regex.length() - 1) == ' ')
                regex.setLength(regex.length() - 1);
            data.pattern = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
            patternCache.add(data);
        }
    }

    public void processCommand(ICommandSender sender, String cmd)
    {
        if (patternCache == null)
            initializePatterns();
        if (error)
            throw new TranslatedCommandException("Error in shortcut command config");

        String lastError = Translator.translate("Invalid syntax. Could not match command");
        patternLoop: for (PatternData pattern : patternCache)
        {
            Matcher matcher = pattern.pattern.matcher(cmd);
            if (!matcher.matches())
                continue;

            List<String> args = new ArrayList<>();
            for (int i = 0; i < matcher.groupCount(); i++)
            {
                String arg = matcher.group(i + 1);
                args.add(arg);
                switch (pattern.argumentTypes.get(i))
                {
                case PLAYER:
                    if (UserIdent.getPlayerByMatchOrUsername(sender, arg) == null)
                    {
                        lastError = Translator.format("Could not find player %s", arg);
                        continue patternLoop;
                    }
                    break;
                case GROUP:
                    if (!APIRegistry.perms.groupExists(arg))
                    {
                        lastError = Translator.format("Could not find player %s", arg);
                        continue patternLoop;
                    }
                    break;
                case ZONE:
                    if (APIRegistry.perms.getZoneById(arg) == null)
                    {
                        lastError = Translator.format("Could not find zone %s", arg);
                        continue patternLoop;
                    }
                    break;
                case DECIMAL:
                case FLOAT:
                case NONE:
                    break;
                }
            }
            processCommand(sender, args, pattern);
            return;
        }
        throw new CommandException(lastError);
    }

    protected void processCommand(ICommandSender sender, List<String> args, PatternData pattern)
    {
        try
        {
            ScriptParser.run(pattern.script, sender, args);
        }
        catch (MissingPlayerException e)
        {
            throw new TranslatedCommandException("Error in script of pattern command %s", name);
        }
        catch (MissingArgumentException e)
        {
            throw new TranslatedCommandException("Error in script of pattern command %s", name);
        }
        catch (MissingPermissionException e)
        {
            throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);
        }
        catch (ScriptException e)
        {
            error = true;
            throw new TranslatedCommandException("Error in script of pattern command %s", name);
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
    public RegisteredPermValue getDefaultPermission()
    {
        return permissionLevel;
    }

}
