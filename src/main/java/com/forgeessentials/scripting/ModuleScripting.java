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
import net.minecraft.server.MinecraftServer;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.scripting.ScriptParser.MissingPermissionException;
import com.forgeessentials.scripting.ScriptParser.ScriptArgument;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.scripting.ScriptParser.ScriptMethod;
import com.forgeessentials.scripting.command.CommandTimedTask;
import com.forgeessentials.scripting.command.PatternCommand;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

@FEModule(name = "Scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting extends ServerEventHandler
{

    public static enum ServerEventType
    {
        START, STOP, LOGIN, LOGOUT;
    }

    @FEModule.ModuleDir
    public static File moduleDir;

    // Map < event name, List of scripts < lines of code > >
    public Map<ServerEventType, Map<String, List<String>>> scripts = new HashMap<>();

    @SubscribeEvent
    public void load(FEModuleInitEvent event)
    {
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
            OutputHandler.felog.info("Unable to write script arguments file");
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
            OutputHandler.felog.info("Unable to write script arguments file");
        }
    }

    public void loadScripts()
    {
        scripts = new HashMap<>();
        for (ServerEventType eventType : ServerEventType.values())
        {
            Map<String, List<String>> scriptList = new HashMap<>();
            scripts.put(eventType, scriptList);

            File path = new File(moduleDir, eventType.toString().toLowerCase());
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
                    OutputHandler.felog.severe(String.format("Error reading script %s", file.getName()));
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
        runEventScripts(ServerEventType.START, null);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        runEventScripts(ServerEventType.STOP, null);
    }

    public static void createDefaultPatternCommands()
    {
        PatternCommand cmd;
        if (!PatternCommand.patternCommands.containsKey("god"))
        {
            cmd = new PatternCommand("god", "/god on|off [player]", null);
            cmd.getPatterns().put("on @p", Arrays.asList(new String[] { //
                    "permcheck fe.commands.god.others", "/p user @1 deny fe.protection.damageby.*", "/heal @player", "echo God mode turned ON for @1", }));
            cmd.getPatterns().put("off @p", Arrays.asList(new String[] { //
                    "permcheck fe.commands.god.others", "/p user %@ clear fe.protection.damageby.*", "echo God mode turned OFF for @1", }));
            cmd.getPatterns().put("on", Arrays.asList(new String[] { //
                    "permcheck fe.commands.god", "/p user @player deny fe.protection.damageby.*", "/heal", "echo God mode ON", }));
            cmd.getPatterns().put("off", Arrays.asList(new String[] { //
                    "/p user @player clear fe.protection.damageby.*", "echo God mode OFF", }));
            cmd.getPatterns().put("", Arrays.asList(new String[] { //
                    "echo Usage: /god on|off [player]", }));
        }
    }

    @FEModule.Reload
    public void reload(ICommandSender sender)
    {
        PatternCommand.loadAll();
    }

    public void runEventScripts(ServerEventType eventType, ICommandSender sender)
    {
        if (sender == null)
            sender = MinecraftServer.getServer();
        for (Entry<String, List<String>> script : scripts.get(eventType).entrySet())
        {
            try
            {
                ScriptParser.run(script.getValue(), sender);
            }
            catch (CommandException e)
            {
                OutputHandler.chatError(sender, e.getMessage());
                // OutputHandler.felog.info(String.format("Error in script \"%s\": %s", script.getKey(),
                // e.getMessage()));
            }
            catch (MissingPermissionException e)
            {
                if (!e.getMessage().isEmpty())
                    OutputHandler.chatError(sender, e.getMessage());
            }
            catch (ScriptException e)
            {
                OutputHandler.felog.severe(String.format("Error in script \"%s\": %s", script.getKey(), e.getMessage()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        runEventScripts(ServerEventType.LOGIN, event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        runEventScripts(ServerEventType.LOGOUT, event.player);
    }

}
