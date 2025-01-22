package com.forgeessentials.jscripting;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.ScriptHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.jscripting.command.CommandJScript;
import com.forgeessentials.jscripting.wrapper.JsLocalStorage;
import com.forgeessentials.jscripting.wrapper.ScriptExtensionRoot;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "JScripting", parentMod = ForgeEssentials.class, isCore = false, canDisable = false)
public class ModuleJScripting extends ServerEventHandler implements ScriptHandler
{

    public static final long CRON_CHECK_INTERVAL = 1000;

    public static final String COMMANDS_DIR = "commands/";

    public static final String PERM = "fe.jscript";

    private static final ScriptEngineManager SEM = new ScriptEngineManager(ForgeEssentials.javaVersion > 8 ? Thread.currentThread().getContextClassLoader() : null );

    @FEModule.Instance
    protected static ModuleJScripting instance;

    @FEModule.ModuleDir
    static File moduleDir;

    public static boolean isNashorn;

    public static boolean isRhino;

    /**
     * Script cache
     */
    protected static Map<File, ScriptInstance> scripts = new HashMap<>();

    /* ------------------------------------------------------------ */

    public static ModuleJScripting instance()
    {
        return instance;
    }

    @Preconditions
    public boolean canLoad()
    {
        System.setProperty("nashorn.args", "-strict --no-java --no-syntax-extensions");
        ScriptEngine engine = SEM.getEngineByName("JavaScript");

        if (engine == null) {
            engine = SEM.getEngineByName("nashorn");
        }

        if (engine == null) {
            engine = SEM.getEngineByName("rhino");
        }

        if (engine == null)
        {
            return false;
        }

        LoggingHandler.felog.info("Loaded Engine: " + engine.getFactory().getEngineName());
        isNashorn = engine.getFactory().getEngineName().toLowerCase().contains("nashorn");
        isRhino = engine.getFactory().getEngineName().toLowerCase().contains("rhino");

        return isNashorn || isRhino;
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
        try
        {
            copyResourceFileIfNotExists("mc.d.ts");
            copyResourceFileIfNotExists("fe.d.ts");
            copyResourceFileIfNotExists("tsconfig.json");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ScriptCompiler.registerExtension(new ScriptExtensionRoot());
        ScriptCompiler.registerExtension(new com.forgeessentials.jscripting.fewrapper.ScriptExtensionRoot());
    }

    private void copyResourceFileIfNotExists(String fileName) throws IOException
    {
        File file = new File(moduleDir, fileName);
        if (!file.exists())
            FileUtils.copyInputStreamToFile(ModuleJScripting.class.getResourceAsStream(fileName), file);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent event)
    {
        LoggingHandler.felog.error(this);
        JsLocalStorage.load();
        loadScripts(MinecraftServer.getServer());
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent event)
    {
        // loadScripts();
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        unloadScripts();
        JsLocalStorage.save();
    }

    @SubscribeEvent
    public void reload(ConfigReloadEvent event)
    {
        reloadScripts(MinecraftServer.getServer());
    }

    public void reloadScripts(ICommandSender sender)
    {
        unloadScripts();
        loadScripts(sender);
    }

    public void unloadScripts()
    {
        for (ScriptInstance script : scripts.values())
            script.dispose();
        scripts.clear();
    }

    public void loadScripts(ICommandSender sender)
    {
        for (Iterator<File> it = FileUtils.iterateFiles(moduleDir, new String[] { "js" }, true); it.hasNext(); )
        {
            File file = it.next();
            if (scripts.containsKey(file))
                continue;
            try
            {
                getScript(file);
            }
            catch (IOException | ScriptException e)
            {
                String scriptName = file.getName();
                ChatOutputHandler.chatError(sender, String.format("FE Script error in %s:", scriptName));
                ChatOutputHandler.chatError(sender, e.getMessage());
                LoggingHandler.felog.error(String.format("FE Script error in %s: %s", scriptName, e.getMessage()));
            }
        }
    }

    /* ------------------------------------------------------------ */

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
                result = scripts.remove(file);
                if (result != null)
                    result.dispose();
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

    public static Collection<ScriptInstance> getScripts()
    {
        return scripts.values();
    }

    public static File getModuleDir()
    {
        return moduleDir;
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
        JsICommandSender jsSender = JsICommandSender.get(sender);
        String fnName = "on" + StringUtils.capitalize(key);
        for (ScriptInstance script : scripts.values())
        {
            try
            {
                if (!script.hasGlobalCallFailed(fnName))
                    script.tryCallGlobal(fnName, jsSender);
            }
            catch (ScriptException e)
            {
                e.printStackTrace();
            }
        }
    }

}
