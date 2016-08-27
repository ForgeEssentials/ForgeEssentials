package com.forgeessentials.jscripting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.ScriptHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.jscripting.command.CommandJScript;
import com.forgeessentials.jscripting.wrapper.JsBlockStatic;
import com.forgeessentials.jscripting.wrapper.JsServerStatic;
import com.forgeessentials.jscripting.wrapper.JsWorldStatic;
import com.forgeessentials.scripting.ScriptParser.ScriptErrorException;
import com.forgeessentials.scripting.ScriptParser.ScriptException;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

@FEModule(name = "JScripting", parentMod = ForgeEssentials.class, isCore = false, canDisable = false)
public class ModuleJScripting extends ServerEventHandler implements ScriptHandler
{

    public static final long CRON_CHECK_INTERVAL = 1000;

    public static final String PERM = "fe.jscript";

    private static final ScriptEngineManager SEM = new ScriptEngineManager(null);

    @FEModule.ModuleDir
    private static File moduleDir;

    private static File commandsDir;

    private static ScriptEngine engine;

    /**
     * Map < event name, List of scripts < lines of code > >
     */
    protected Map<String, Map<String, String>> scripts = new HashMap<>();

    /* ------------------------------------------------------------ */

    @Preconditions
    public boolean canLoad()
    {
        System.setProperty("nashorn.args", "-strict --no-java --no-syntax-extensions");
        engine = SEM.getEngineByName("JavaScript");
        return engine != null;
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent event)
    {
        Bindings scope = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
        scope.put("Server", new JsServerStatic());
        scope.put("Block", new JsBlockStatic());
        scope.put("World", new JsWorldStatic());

        // APIRegistry.scripts = this;
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent event)
    {
        FECommandManager.registerCommand(new CommandJScript());

        commandsDir = new File(moduleDir, "commands");
        commandsDir.mkdirs();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
        // Reinitialize MC binding because MinecraftServer.getServer() changed
        Bindings scope = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
        scope.put("mc", new JsServerStatic());
        
        // TODO: Load server scripts
        // TODO: Load scripted commands
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void serverTickEvent(ServerTickEvent event)
    {
        if (event.phase == Phase.START)
            return;
        // TODO: Handle cron scripts
    }

    @SubscribeEvent
    public void reload(ConfigReloadEvent event)
    {
        // TODO: Reload server scripts
        // TODO: Reload scripted commands
    }

    /* ------------------------------------------------------------ */
    /* Script handling OLD */

    public static ScriptEngine getEngine()
    {
        // engine = SEM.getEngineByName("JavaScript");
        return engine;
    }

    public static File getCommandsDir()
    {
        return commandsDir;
    }

    /* ------------------------------------------------------------ */
    /* Script handling OLD */

    @Override
    public void addScriptType(String key)
    {
        if (!scripts.containsKey(key))
            scripts.put(key, new HashMap<String, String>());
    }

    @Override
    public void runEventScripts(String eventType, ICommandSender sender)
    {
        if (sender == null)
            sender = MinecraftServer.getServer();
        for (Entry<String, String> script : scripts.get(eventType).entrySet())
        {
            if (script.getValue().isEmpty())
                continue;
            try
            {
                // TODO: Run event script
                // ScriptParser.run(script.getValue(), sender);
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

}
