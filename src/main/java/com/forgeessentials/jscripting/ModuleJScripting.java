package com.forgeessentials.jscripting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.command.ICommandSender;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.ScriptHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.jscripting.command.CommandJScript;
import com.forgeessentials.jscripting.wrapper.JsCommandSender;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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

    public static boolean isNashorn;

    public static boolean isRhino;

    /**
     * Script cache
     */
    protected static Map<File, ScriptInstance> scripts = new HashMap<>();

    /* ------------------------------------------------------------ */

    @Preconditions
    public boolean canLoad()
    {
        System.setProperty("nashorn.args", "-strict --no-java --no-syntax-extensions");
        ScriptEngine engine = SEM.getEngineByName("JavaScript");
        isNashorn = engine.getFactory().getEngineName().toLowerCase().contains("nashorn");
        isRhino = engine.getFactory().getEngineName().toLowerCase().contains("rhino");
        return engine != null;
    }

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent event)
    {
        APIRegistry.scripts = this;
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
        for (Iterator<File> it = FileUtils.iterateFiles(moduleDir, new String[] { "js" }, true); it.hasNext();)
        {
            File file = it.next();
            try
            {
                getScript(file);
            }
            catch (IOException | ScriptException e)
            {
                LoggingHandler.felog.error("FE Script error: " + e.getMessage());
            }
        }
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
        return SEM.getEngineByName("JavaScript");
    }

    public static Compilable getCompilable()
    {
        return (Compilable) getEngine();
    }

    public static synchronized ScriptInstance getScript(File file) throws IOException, ScriptException
    {
        ScriptInstance result = scripts.get(file);
        if (result == null)
        {
            result = new ScriptInstance(file);
            scripts.put(file, result);
        }
        else
        {
            try
            {
                result.checkIfModified();
            }
            catch (IOException | ScriptException e)
            {
                scripts.remove(file);
                throw e;
            }
        }
        return result;
    }

    public static ScriptInstance getScript(String uri) throws IOException, ScriptException
    {
        File f = new File(moduleDir, uri);
        if (!f.exists())
            return null;
        return getScript(f);
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
        String fnName = "on" + StringUtils.capitalize(key);
        try
        {
            new File(moduleDir, fnName + ".txt").createNewFile();
        }
        catch (IOException e)
        {
            /* nothing */
        }
    }

    @Override
    public synchronized void runEventScripts(String key, ICommandSender sender)
    {
        JsCommandSender jsSender = sender == null ? null : new JsCommandSender(sender);
        String fnName = "on" + StringUtils.capitalize(key);
        for (ScriptInstance script : scripts.values())
        {
            try
            {
                if (!script.illegalFunction(fnName))
                    script.call(fnName, jsSender);
            }
            catch (NoSuchMethodException | ScriptException e)
            {
                e.printStackTrace();
            }
        }
    }

}
