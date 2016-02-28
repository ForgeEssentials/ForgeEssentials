package com.forgeessentials.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.ScriptHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;
import com.forgeessentials.scripting.ScriptParser.ScriptErrorException;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.ScriptMethod;
import com.forgeessentials.scripting.command.PatternCommand;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@FEModule(name = "Scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting extends ServerEventHandler implements ScriptHandler
{

    public static final long CRON_CHECK_INTERVAL = 1000;

    @FEModule.ModuleDir
    protected static File moduleDir;

    protected static File commandsDir;

    protected long lastCronCheck;

    /**
     * Map < event name, List of scripts < lines of code > >
     */
    protected Map<String, Map<String, List<String>>> scripts = new HashMap<>();

    /**
     * Stores the time that scripts ran the last time
     */
    protected Map<String, Long> cronTimes = new HashMap<>();

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent event)
    {
        APIRegistry.scripts = this;
        new ScriptEventHandler();

    }

    @SubscribeEvent
    public void load(FEModuleInitEvent event)
    {
        commandsDir = new File(moduleDir, "commands");
        commandsDir.mkdirs();

        try (PrintWriter writer = new PrintWriter(new File(moduleDir, "arguments.txt")))
        {
            writer.println("# Script arguments");
            writer.println();
            SortedMap<String, ScriptArgument> sortedItems = new TreeMap<>(ScriptArguments.getAll());
            for (Entry<String, ScriptArgument> item : sortedItems.entrySet())
            {
                writer.println("## @" + item.getKey());
                writer.println(item.getValue().getHelp());
                writer.println();
            }
        }
        catch (FileNotFoundException e)
        {
            LoggingHandler.felog.info("Unable to write script arguments file");
        }
        try (PrintWriter writer = new PrintWriter(new File(moduleDir, "methods.txt")))
        {
            writer.println("# Script methods");
            writer.println();
            SortedMap<String, ScriptMethod> sortedItems = new TreeMap<>(ScriptMethods.getAll());
            for (Entry<String, ScriptMethod> item : sortedItems.entrySet())
            {
                writer.println("## " + item.getKey());
                writer.println(item.getValue().getHelp());
                writer.println();
            }
        }
        catch (FileNotFoundException e)
        {
            LoggingHandler.felog.info("Unable to write script arguments file");
        }
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
        reloadScripts();
        PatternCommand.loadAll();
        createDefaultPatternCommands();
        PatternCommand.saveAll();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void serverTickEvent(ServerTickEvent event)
    {
        if (event.phase != Phase.START)
            runCronScripts();
    }

    @SubscribeEvent
    public void reload(ConfigReloadEvent event)
    {
        PatternCommand.deregisterAll();

        reloadScripts();
        PatternCommand.loadAll();
        createDefaultPatternCommands();
        PatternCommand.saveAll();
    }

    public static File getPatternCommandsDir()
    {
        return commandsDir;
    }

    /* ------------------------------------------------------------ */
    /* Script handling */

    @Override
    public void addScriptType(String key)
    {
        if (!scripts.containsKey(key))
            scripts.put(key, new HashMap<String, List<String>>());
    }

    public void reloadScripts()
    {
        for (Entry<String, Map<String, List<String>>> entry : scripts.entrySet())
        {
            Map<String, List<String>> scriptList = entry.getValue();
            scriptList.clear();

            File path = new File(moduleDir, entry.getKey().toLowerCase());
            if (!path.exists())
            {
                path.mkdirs();
                continue;
            }
            // File[] files = path.listFiles(new FilenameFilter() { @Override public boolean accept(File dir, String
            // name) { return FilenameUtils.getExtension(name).equalsIgnoreCase("txt");}});
            for (File file : path.listFiles())
            {
                List<String> script = new ArrayList<>();
                try
                {
                    for (String line : FileUtils.readLines(file))
                        script.add(line);
                    scriptList.put(file.getName(), script);
                    if (entry.getKey().equals("cron"))
                        cronTimes.put(file.getName(), System.currentTimeMillis());
                }
                catch (IOException e1)
                {
                    LoggingHandler.felog.error(String.format("Error reading script %s", file.getName()));
                    continue;
                }
            }
        }
    }

    public void createDefaultPatternCommands()
    {
        PatternCommand cmd;
        if (!PatternCommand.getPatternCommands().containsKey("god"))
        {
            cmd = new PatternCommand("god", "/god on|off [player]", null);
            cmd.getPatterns().put("on @p", Arrays.asList(new String[] { "permcheck fe.commands.god.others", //
                    "permset user @0 deny fe.protection.damageby.*", "$*/heal @player", "echo God mode turned ON for @0" }));
            cmd.getPatterns()
                    .put("off @p",
                            Arrays.asList(new String[] { //
                                    "permcheck fe.commands.god.others", "permset user %@ clear fe.protection.damageby.*",
                                    "echo God mode turned OFF for @0", }));
            cmd.getPatterns().put("on", Arrays.asList(new String[] { //
                    "permcheck fe.commands.god", "permset user @player deny fe.protection.damageby.*", "$*/heal", "echo God mode ON", }));
            cmd.getPatterns().put("off", Arrays.asList(new String[] { //
                    "permset user @player clear fe.protection.damageby.*", "echo God mode OFF", }));
            cmd.getPatterns().put("", Arrays.asList(new String[] { //
                    "echo Usage: /god on|off [player]", }));
            cmd.getExtraPermissions().put("fe.commands.god", PermissionLevel.OP);
            cmd.registerExtraPermissions();
        }
    }

    @Override
    public void runEventScripts(String eventType, ICommandSender sender)
    {
        if (sender == null)
            sender = MinecraftServer.getServer();
        for (Entry<String, List<String>> script : scripts.get(eventType).entrySet())
        {
            if (script.getValue().isEmpty())
                continue;
            try
            {
                ScriptParser.run(script.getValue(), sender);
            }
            catch (CommandException | ScriptErrorException e)
            {
                if (e.getMessage() != null && !e.getMessage().isEmpty())
                    ChatOutputHandler.chatError(sender, e.getMessage());
            }
            catch (ScriptException e)
            {
                LoggingHandler.felog.error(String.format("Error in script \"%s\": %s", script.getKey(), e.getMessage()));
            }
        }
    }

    public void runCronScripts()
    {
        if (System.currentTimeMillis() - lastCronCheck >= CRON_CHECK_INTERVAL)
        {
            lastCronCheck = System.currentTimeMillis();
            for (Entry<String, List<String>> script : scripts.get(ScriptEventHandler.SCRIPTKEY_CRON).entrySet())
            {
                List<String> lines = new ArrayList<>(script.getValue());
                if (lines.size() < 2)
                    continue;
                String cronDef = lines.remove(0);
                if (!checkCron(script.getKey(), cronDef))
                    continue;
                try
                {
                    cronTimes.put(script.getKey(), System.currentTimeMillis());
                    ScriptParser.run(lines, MinecraftServer.getServer());
                    break;
                }
                catch (CommandException | ScriptErrorException e)
                {
                    if (e.getMessage() != null && !e.getMessage().isEmpty())
                        ChatOutputHandler.chatError(MinecraftServer.getServer(), e.getMessage());
                }
                catch (ScriptException e)
                {
                    LoggingHandler.felog.error(String.format("Error in script \"%s\": %s", script.getKey(), e.getMessage()));
                }
            }
        }
    }

    public boolean checkCron(String jobName, String cronDef)
    {
        cronDef.trim();
        if (cronDef.charAt(0) != '#')
            return false; // error
        cronDef = cronDef.substring(1).trim();

        long interval;
        try
        {
            interval = Long.parseLong(cronDef);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        long lastTime = cronTimes.containsKey(jobName) ? cronTimes.get(jobName) : 0;
        long nextTime = lastTime + interval * 1000;
        return nextTime <= System.currentTimeMillis();
    }
}
