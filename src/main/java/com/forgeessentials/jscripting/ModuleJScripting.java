package com.forgeessentials.jscripting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.ScriptHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.jscripting.command.CommandJScript;
import com.forgeessentials.jscripting.wrapper.JsBlockStatic;
import com.forgeessentials.jscripting.wrapper.JsServerStatic;
import com.forgeessentials.jscripting.wrapper.JsWorldStatic;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

@FEModule(name = "JScripting", parentMod = ForgeEssentials.class, isCore = false, canDisable = false)
public class ModuleJScripting extends ServerEventHandler implements ScriptHandler
{

    public static final long CRON_CHECK_INTERVAL = 1000;

    public static final String COMMANDS_DIR = "commands/";

    public static final String PERM = "fe.jscript";

    private static final ScriptEngineManager SEM = new ScriptEngineManager(null);

    @FEModule.ModuleDir
    private static File moduleDir;

    private static File commandsDir;

    private static ScriptEngine engine;

    /**
     * Script cache
     */
    protected static Map<String, ScriptInstance> scripts = new HashMap<>();

    /**
     * Map < event name, List of scripts < lines of code > >
     */
    // protected Map<String, Map<String, String>> eventScripts = new HashMap<>();

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

        commandsDir = new File(moduleDir, COMMANDS_DIR);
        commandsDir.mkdirs();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
        // TODO: Load server scripts
        // TODO: Load scripted commands
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void serverTickEvent(ServerTickEvent event)
    {
        if (event.phase == Phase.START)
            return;

        // TODO: Handle cron scripts - probably not even necessary
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        scripts.clear();
    }

    @SubscribeEvent
    public void reload(ConfigReloadEvent event)
    {
        scripts.clear();

        // TODO: Reload scripted commands
    }

    /* ------------------------------------------------------------ */
    /* Script handling OLD */

    public static ScriptEngine getEngine()
    {
        return engine;
    }

    public static Compilable getCompiler()
    {
        return (Compilable) engine;
    }

    public static ScriptInstance getScript(String uri) throws IOException, ScriptException
    {
        ScriptInstance result = scripts.get(uri);
        if (result == null)
        {
            File f = new File(moduleDir, uri);
            if (!f.exists())
                return null;
            result = new ScriptInstance(f);
            scripts.put(uri, result);
        }
        else
        {
            try
            {
                result.checkIfModified();
            }
            catch (IOException | ScriptException e)
            {
                scripts.remove(uri);
                throw e;
            }
        }
        return result;
    }

    public static File getCommandsDir()
    {
        return commandsDir;
    }

    /* ------------------------------------------------------------ */
    /* Script handling API */

    @Override
    public void addScriptType(String key)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void runEventScripts(String key, ICommandSender sender)
    {
        // TODO Auto-generated method stub
    }

}
