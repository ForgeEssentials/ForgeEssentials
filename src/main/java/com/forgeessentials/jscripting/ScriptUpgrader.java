package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class ScriptUpgrader
{

    public static boolean OVERWRITE_EXISTING = true;

    public static final String[] UPGRADE_EVENTS = {
            "afk",
            "afkreturn",
            "cron",
            "death",
            "interact_left",
            "interact_right",
            "interact_use",
            "login",
            "logout",
            "playerdeath",
            "respawn",
            "sleep",
            "start",
            "stop",
            "wake"
    };

    public static void upgradeOldScripts(ICommandSender sender)
    {
        File baseDir = new File(ForgeEssentials.getFEDirectory(), "Scripting");
        if (!baseDir.exists())
            return;

        int count = 0;
        for (String eventType : UPGRADE_EVENTS)
        {
            File dir = new File(baseDir, eventType);
            if (!dir.exists())
                continue;
            File outDir = new File(ModuleJScripting.moduleDir, eventType);

            for (Iterator<File> it = FileUtils.iterateFiles(dir, new String[] { "txt" }, true); it.hasNext();)
            {
                File file = it.next();
                String scriptName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                File outFile = new File(outDir, scriptName + ".js");
                if (!OVERWRITE_EXISTING && outFile.exists())
                {
                    ChatOutputHandler.chatNotification(sender, "Already upgraded: " + scriptName);
                    continue;
                }
                try
                {
                    List<String> lines = FileUtils.readLines(file);
                    if (lines.isEmpty())
                        continue;

                    StringBuilder newScript = upgradeOldScript(eventType, lines);
                    if (newScript != null)
                    {
                        outDir.mkdirs();
                        try (Writer writer = new FileWriter(outFile))
                        {
                            writer.write(newScript.toString());
                            count++;
                            ChatOutputHandler.chatConfirmation(sender, "Upgraded: " + scriptName);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (IOException e)
                {
                    String msg = String.format("Could upgrade script %s: %s", file.getName(), e.getMessage());
                    LoggingHandler.felog.error(msg);
                    ChatOutputHandler.chatError(sender, msg);
                }
                catch (Exception e)
                {
                    String msg = String.format("Could upgrade script %s: %s", file.getName(), e.getMessage());
                    LoggingHandler.felog.error(msg);
                    ChatOutputHandler.chatError(sender, msg);
                }
            }
        }

        File commandsDir = new File(baseDir, "commands");
        if (commandsDir.exists())
        {
            File outDir = new File(ModuleJScripting.moduleDir, "commands");
            Map<String, PatternCommand> patternCommands = DataManager.loadAll(PatternCommand.class, commandsDir);
            for (Entry<String, PatternCommand> cmd : patternCommands.entrySet())
            {
                String scriptName = cmd.getKey();
                File outFile = new File(outDir, scriptName + ".js");
                if (!OVERWRITE_EXISTING && outFile.exists())
                {
                    ChatOutputHandler.chatNotification(sender, "Already upgraded: " + scriptName);
                    continue;
                }

                try
                {
                    StringBuilder newScript = updatePatternCommand(cmd.getValue());
                    if (newScript != null)
                    {
                        outDir.mkdirs();
                        try (Writer writer = new FileWriter(outFile))
                        {
                            writer.write(newScript.toString());
                            count++;
                            ChatOutputHandler.chatConfirmation(sender, "Upgraded: " + scriptName);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    String msg = String.format("Could upgrade script %s: %s", scriptName, e.getMessage());
                    LoggingHandler.felog.error(msg);
                    ChatOutputHandler.chatError(sender, msg);
                }
            }
        }

        ChatOutputHandler.chatConfirmation(sender, "Upgraded " + count + " old scripts");
    }

    private static StringBuilder updatePatternCommand(PatternCommand cmd) throws Exception
    {
        StringBuilder out = new StringBuilder();
        out.append("function cmd(args) {\n\t");
        out.append("var sender = args.sender;\n\t");
        out.append("var argsLine = args.toString();\n\t");
        out.append("var match;\n\t");
        List<Entry<String, List<String>>> sortedPatterns = new ArrayList<>(cmd.patterns.entrySet());
        sortedPatterns.sort((a, b) -> b.getKey().split(" ").length - a.getKey().split(" ").length);
        for (Entry<String, List<String>> pattern : sortedPatterns)
        {
            out.append("if (");
            upgradePattern(out, pattern.getKey());
            out.append(") {");
            // out.append("\n\t\targs.confirm(JSON.stringify(match));");
            for (String line : pattern.getValue())
            {
                if (line.isEmpty())
                    continue;
                out.append("\n\t\t");
                upgradeAction(out, line);
            }
            out.append("\n\t} else ");
        }
        out.append(" {");
        out.append("\n\t\targs.error('Invalid command syntax');\n\t}");
        out.append("\n}");
        out.append("\n\t");
        out.append("\nFEServer.registerCommand({");
        out.append("\n\tname: '" + cmd.name + "',");
        out.append("\n\tusage: '" + cmd.usage + "',");
        out.append("\n\tpermission: " + cmd.permission + ",");
        out.append("\n\topOnly: " + (cmd.permissionLevel == DefaultPermissionLevel.OP ? "true" : "false") + ",");
        out.append("\n\tprocessCommand: cmd,");
        // out.append("\n\ttabComplete: processCommand,");
        out.append("\n});");
        out.append("\n");
        return out;
    }

    private static void upgradePattern(StringBuilder out, String pattern)
    {
        out.append("match = argsLine.match(/^");
        boolean mustEnd = false;
        boolean first = true;
        for (String part : pattern.split(" "))
        {
            if (part.isEmpty())
                continue;
            if (mustEnd)
                throw new IllegalArgumentException("Pattern must end after @*");
            if (!first)
                out.append("\\s+");
            first = false;
            if (part.charAt(0) == '@')
            {
                switch (part.substring(1))
                {
                case "f":
                    out.append("([+-]?\\d+(?:\\.\\d+)?)");
                    // argumentTypes.add(ArgumentType.FLOAT);
                    break;
                case "d":
                    out.append("([+-]?\\d+)");
                    // argumentTypes.add(ArgumentType.DECIMAL);
                    break;
                case "p":
                case "player":
                    out.append("(\\S+)");
                    // argumentTypes.add(ArgumentType.PLAYER);
                    break;
                case "g":
                case "group":
                    out.append("(\\S+)");
                    // argumentTypes.add(ArgumentType.GROUP);
                    break;
                case "zone":
                    out.append("(\\d+)");
                    // argumentTypes.add(ArgumentType.ZONE);
                    break;
                case "*":
                    out.append("(.*)");
                    // argumentTypes.add(ArgumentType.REST);
                    mustEnd = true;
                    break;
                case "+":
                    out.append("(.+)");
                    // argumentTypes.add(ArgumentType.REST);
                    mustEnd = true;
                    break;
                case "":
                    out.append("(\\S+)");
                    // argumentTypes.add(ArgumentType.NONE);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown pattern argument type %s ", part.substring(1)));
                }
            }
            else
            {
                out.append(part);
                // regex.append(java.util.regex.Pattern.quote(part));
            }
        }
        // Cut off final space
        if (out.length() > 0 && out.charAt(out.length() - 1) == ' ')
            out.setLength(out.length() - 1);
        out.append("$/)");
    }

    public static StringBuilder upgradeOldScript(String eventType, List<String> lines) throws Exception
    {
        StringBuilder out = new StringBuilder();

        long cronInterval = 0;
        if (eventType.equals("cron"))
        {
            cronInterval = checkCron(lines.remove(0));
            if (cronInterval <= 0 || lines.isEmpty())
                return null;
            out.append("setInterval(function() {");
        }
        else
        {
            out.append("function on");
            out.append(StringUtils.capitalize(eventType));
            out.append("(sender) {");
        }

        upgradeActions(lines, out);

        // Close function
        if (cronInterval > 0)
        {
            out.append("\n}, ");
            out.append(cronInterval * 1000);
            out.append(");\n");
        }
        else
            out.append("\n}\n");

        return out;
    }

    private static void upgradeActions(List<String> lines, StringBuilder out) throws Exception
    {
        for (String line : lines)
        {
            out.append("\n\t");
            if (line.isEmpty())
                continue;
            upgradeAction(out, line);
        }
    }

    private static void upgradeAction(StringBuilder out, String line) throws Exception
    {

        List<String> args = new ArrayList<>(Arrays.asList(line.split(" ")));
        String command = args.remove(0);

        upgradeOldScriptArgs(args);

        char c = command.charAt(0);
        if (c == '/' || c == '$' || c == '?' || c == '*')
        {
            // Handle MC commands
            boolean hideChat = false;
            boolean ignoreErrors = false;
            boolean asServer = false;
            modifierLoop: while (true)
            {
                command = command.substring(1);
                switch (c)
                {
                case '$':
                    asServer = true;
                    break;
                case '?':
                    ignoreErrors = true;
                    break;
                case '*':
                    hideChat = true;
                    break;
                case '/':
                    break modifierLoop;
                default:
                    throw new Exception(String.format("Could not handle script action \"%s\"", command));
                }
                c = command.charAt(0);
            }
            out.append(ignoreErrors ? "Server.tryRunCommand(sender" : "Server.runCommand(sender");
            if (hideChat || asServer)
            {
                out.append(".doAs(");
                out.append(asServer ? "null" : "sender.getPlayer()");
                out.append(hideChat ? ", true)" : ", false)");
            }
            out.append(", '");
            out.append(command);
            out.append("'");
            for (String arg : args)
            {
                out.append(", ");
                out.append(arg);
            }
            out.append(");");
        }
        else
        {
            // TODO: Handle other commands
            switch (command)
            {
            case "set":
                out.append("var ");
                out.append(args.remove(0));
                out.append(" = ");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(";");
                break;
            // case "permcheck":
            // break;
            case "echo":
                out.append("sender.chat(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "confirm":
                out.append("sender.chatConfirm(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "notify":
                out.append("sender.chatNotification(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "warn":
                out.append("sender.chatWarning(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "error":
                out.append("sender.chatError(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "fail":
                out.append("sender.chatError(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");\n\treturn;");
                break;
            case "echoall":
                out.append("Server.chat(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "confirmall":
                out.append("Server.chatConfirm(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "notifyall":
                out.append("Server.chatNotification(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "warnall":
                out.append("Server.chatWarning(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "errorall":
                out.append("Server.chatError(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "failall":
                out.append("Server.chatError(");
                out.append(StringUtils.join(args, " + ' ' + "));
                out.append(");\n\treturn;");
                break;
            case "permset":
                String cmd = args.remove(0);
                switch (cmd) {
                case "'user'":
                    out.append("fe.Permissions.setPlayerPermissionProperty(");
                    break;
                case "'group'":
                    out.append("fe.Permissions.setGroupPermissionProperty(");
                    break;
                }
                out.append("FEServer.getUserIdent(");
                out.append(args.remove(0));
                out.append("), ");
                cmd = args.remove(0);
                out.append(args.remove(0));
                out.append(", ");
                switch (cmd) {
                case "'clear'":
                    out.append("null");
                    break;
                case "'deny'":
                    out.append("false");
                    break;
                case "'allow'":
                    out.append("true");
                    break;
                case "'value'":
                    out.append(args.remove(0));
                    break;
                }
                out.append(");");
                break;
            case "permcheck":
                out.append("if (!fe.Permissions.checkPermission(");
                out.append("sender.getPlayer(), ");
                out.append(args.remove(0));
                out.append(")) return sender.chatError(");
                out.append(args.isEmpty() ? "\"You don't have permission to use this command\"" : StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "permchecksilent":
                out.append("if (!fe.Permissions.checkPermission(");
                out.append("sender.getPlayer(), ");
                out.append(args.remove(0));
                out.append(")) return;");
                break;
            case "!permcheck":
                out.append("if (fe.Permissions.checkPermission(");
                out.append("sender.getPlayer(), ");
                out.append(args.remove(0));
                out.append(")) return sender.chatError(");
                out.append(args.isEmpty() ? "\"You don't have permission to use this command\"" : StringUtils.join(args, " + ' ' + "));
                out.append(");");
                break;
            case "!permchecksilent":
                out.append("if (fe.Permissions.checkPermission(");
                out.append("sender.getPlayer(), ");
                out.append(args.remove(0));
                out.append(", ");
                out.append(args.isEmpty() ? false : args.remove(0));
                out.append(")) return;");
                break;
            case "timeout":
                String timeout = args.remove(0);
                // startTimeout
                out.append("setTimeout(function() {\n\t\t");

                // run command
                out.append("Server.runCommand(sender, '");
                out.append(command);
                out.append("'");
                for (String arg : args)
                {
                    out.append(", ");
                    out.append(arg);
                }
                out.append(");");

                // close handler
                out.append("}, ");
                out.append(timeout);
                out.append(");");
                break;
            default:
                out.append("// ");
                out.append(line);
                break;
            }
        }
    }

    public static void upgradeOldScriptArgs(List<String> args)
    {
        for (int i = 0; i < args.size(); i++)
            args.set(i, upgradeOldScriptArg(args.get(i)));
    }

    public static final Pattern ARGUMENT_PATTERN = Pattern.compile("@([\\w*]+)\\.?");

    public static String upgradeOldScriptArg(String input)
    {
        Matcher matcher = ARGUMENT_PATTERN.matcher(input);
        if (!matcher.find())
            return "'" + input + "'";

        String arg = matcher.group(1).toLowerCase();

        try
        {
            int idx = Integer.parseInt(arg);
            return "match[" + (idx + 1) + "]";
        }
        catch (NumberFormatException e)
        {
            arg = matchScriptArgument(arg);
            if (matcher.start() != 0)
            {
                arg = "'" + input.substring(0, matcher.start()) + "' + ";
            }
            if (matcher.end() != input.length())
            {
                arg = arg + " + '" + input.substring(matcher.end(), input.length()) + "'";
            }
            return arg;
        }
    }

    public static String matchScriptArgument(String arg)
    {
        switch (arg)
        {
        case "sender":
            return "sender.getName()";
        case "player":
            return "sender.getPlayer().getName()";
        case "uuid":
            return "sender.getPlayer().getUuid()";
        case "x":
            return "Math.round(sender.getPlayer().getX())";
        case "y":
            return "Math.round(sender.getPlayer().getY())";
        case "z":
            return "Math.round(sender.getPlayer().getZ())";
        case "xd":
            return "sender.getPlayer().getX()";
        case "yd":
            return "sender.getPlayer().getY()";
        case "zd":
            return "sender.getPlayer().getZ()";
        case "dim":
            return "sender.getPlayer().getDimension()";
        case "gm":
            return "sender.getPlayer().getGameType()";
        case "health":
            return "sender.getPlayer().getHealth()";
        case "hunger":
            return "sender.getPlayer().getFoodLevel()";
        case "saturation":
            return "sender.getPlayer().getSaturationLevel()";
        case "zone":
            return "fe.Permissions.getZoneAt(sender.getPlayer()).getName()";
        case "zoneid":
            return "fe.Permissions.getZoneAt(sender.getPlayer()).getId()";
        default:
            return "'" + arg + "'";
        }
    }

    public static long checkCron(String cronDef)
    {
        cronDef.trim();
        if (cronDef.charAt(0) != '#')
            return 0; // error
        cronDef = cronDef.substring(1).trim();
        try
        {
            return Long.parseLong(cronDef);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    public static class PatternCommand
    {
        protected String name;
        protected String usage;
        protected String permission;
        protected Map<String, DefaultPermissionLevel> extraPermissions = new HashMap<>();
        protected DefaultPermissionLevel permissionLevel = DefaultPermissionLevel.ALL;
        protected Map<String, List<String>> patterns = new HashMap<>();
    }

}
