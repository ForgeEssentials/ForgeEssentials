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

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
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
import com.forgeessentials.scripting.command.CommandTimedTask;
import com.forgeessentials.scripting.command.PatternCommand;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
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
    public static File moduleDir;

    public static File commandsDir;

    protected long lastCronCheck;

    public static List<String> knownEventTypes = new ArrayList<>();

    // Map < event name, List of scripts < lines of code > >
    public Map<String, Map<String, List<String>>> scripts = new HashMap<>();

    protected Map<String, Long> cronTimes = new HashMap<>();

    static
    {
        knownEventTypes.add("start");
        knownEventTypes.add("stop");
        knownEventTypes.add("login");
        knownEventTypes.add("logout");
        knownEventTypes.add("death");
        knownEventTypes.add("cron");
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        APIRegistry.scripts = this;
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
            for (Entry<String, ScriptArgument> item : ScriptArguments.getAll().entrySet())
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
            for (Entry<String, ScriptMethod> item : ScriptMethods.getAll().entrySet())
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

    public void loadScripts()
    {
        scripts = new HashMap<>();
        for (String eventType : knownEventTypes)
        {
            Map<String, List<String>> scriptList = new HashMap<>();
            scripts.put(eventType, scriptList);

            File path = new File(moduleDir, eventType.toLowerCase());
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
                }
                catch (IOException e1)
                {
                    LoggingHandler.felog.error(String.format("Error reading script %s", file.getName()));
                    continue;
                }
            }
        }
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        new CommandTimedTask().register();

        loadScripts();
        PatternCommand.loadAll();
        createDefaultPatternCommands();
        PatternCommand.saveAll();
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        runEventScripts("start", null);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        runEventScripts("stop", null);
    }

    public static void createDefaultPatternCommands()
    {
        PatternCommand cmd;
        if (!PatternCommand.patternCommands.containsKey("god"))
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

    @SubscribeEvent
    public void reload(ConfigReloadEvent e)
    {
        PatternCommand.deregisterAll();

        loadScripts();
        PatternCommand.loadAll();
        createDefaultPatternCommands();
        PatternCommand.saveAll();
    }

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        runEventScripts("login", event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        runEventScripts("logout", event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.entityLiving instanceof EntityPlayerMP)
        {
            runEventScripts("death", (EntityPlayerMP) event.entityLiving);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void serverTickEvent(ServerTickEvent event)
    {
        if (event.phase == Phase.START)
            return;
        if (System.currentTimeMillis() - lastCronCheck >= CRON_CHECK_INTERVAL)
        {
            lastCronCheck = System.currentTimeMillis();
            for (Entry<String, List<String>> script : scripts.get("cron").entrySet())
            {
                List<String> lines = new ArrayList<>(script.getValue());
                if (lines.size() < 2)
                    continue;
                String cronDef = lines.remove(0);
                if (!checkCron(script.getKey(), cronDef))
                    continue;
                try
                {
                    ScriptParser.run(lines, MinecraftServer.getServer());
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

    protected boolean checkCron(String jobName, String cronDef)
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
        if (lastTime + interval * 1000 > System.currentTimeMillis())
            return false;

        cronTimes.put(jobName, System.currentTimeMillis());
        return true;
    }

    public void addScriptType(String key)
    {
        knownEventTypes.add(key);
    }

}
