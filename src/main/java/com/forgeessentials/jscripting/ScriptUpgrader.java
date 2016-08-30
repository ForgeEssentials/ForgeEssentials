package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

public class ScriptUpgrader
{

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

        for (String eventType : UPGRADE_EVENTS)
        {
            File dir = new File(baseDir, eventType);
            if (!dir.exists())
                continue;
            File outDir = new File(ModuleJScripting.moduleDir, eventType);

            for (Iterator<File> it = FileUtils.iterateFiles(dir, new String[] { "txt" }, true); it.hasNext();)
            {
                File file = it.next();
                File outFile = new File(outDir, file.getName().substring(0, file.getName().lastIndexOf('.')) + ".js");
                // if (outFile.exists())
                // continue;
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
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.error(String.format("Could upgrade script %s: %s", file.getName(), e.getMessage()));
                }
                catch (Exception e)
                {
                    LoggingHandler.felog.error(String.format("Could upgrade script %s: %s", file.getName(), e.getMessage()));
                }
            }
        }
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

        for (String line : lines)
        {
            out.append("\n\t");
            if (line.isEmpty())
                continue;

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
                    out.append("doAs(");
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
                    out.append("sender.chatConfirm(");
                    out.append(StringUtils.join(args, " + ' ' + "));
                    out.append(");");
                    break;
                default:
                    out.append("// ");
                    out.append(line);
                    break;
                }
            }
        }

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
            return "args.get(" + idx + ")";
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
        case "player":
            return "sender.getPlayer()";
        case "x":
            return "sender.getPlayer().getX()";
        case "y":
            return "sender.getPlayer().getY()";
        case "z":
            return "sender.getPlayer().getZ()";
        case "dim":
            return "sender.getPlayer().getDimension()";
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

}
