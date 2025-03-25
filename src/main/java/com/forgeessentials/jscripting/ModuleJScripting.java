package com.forgeessentials.jscripting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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

    private static final ScriptEngineManager SEM = new ScriptEngineManager(null);

    private static final String DEFAULT_NASHORN_ARGS = "-strict --no-java --no-syntax-extensions";

    private static String nashornArgs;

    private static ScriptEngineFactory factory;

    @FEModule.Instance
    protected static ModuleJScripting instance;

    @FEModule.ModuleDir
    static File moduleDir;

    public static boolean isNashorn;

    public static boolean isRhino;

    static {
        nashornArgs = System.getProperty("fe.nashorn.args");
        if (nashornArgs == null) {
            nashornArgs = DEFAULT_NASHORN_ARGS;
        }
    }

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
        ScriptEngine engine = SEM.getEngineByName("JavaScript");
        if (engine != null && (factory = engine.getFactory()) != null)
        {
            isNashorn = factory.getEngineName().toLowerCase().contains("nashorn");
            isRhino = factory.getEngineName().toLowerCase().contains("rhino");
        }
        return factory != null;
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void save(WorldEvent.Save event) {
        //TODO: Refactor localstorage to only save on world save instead of on every update
        if (event.getWorld() == FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld())
        {
            for (ScriptInstance instance : scripts.values())
            {
                instance.saveConfig();
            }
        }
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
        JsLocalStorage.load();
        loadScripts(FMLCommonHandler.instance().getMinecraftServerInstance());
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
        reloadScripts(FMLCommonHandler.instance().getMinecraftServerInstance());
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
        for (Iterator<File> it = FileUtils.iterateFiles(moduleDir, new String[] { "js", "ts" }, true); it.hasNext(); )
        {
            File file = it.next();
            String name = file.getName();
            if (!name.endsWith("d.ts") && name.endsWith("ts")) {
                LoggingHandler.felog.warn("Typescript file: {} found! This file must be transpiled to javascript with the js extension.  This file will be ignored.", name);
                continue;
            }
            if (name.endsWith("d.ts") || scripts.containsKey(file))
                continue;
            try
            {
                getScript(file);
            }
            catch (CommandException | IOException | ScriptException e)
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
        if (isNashorn)
        {
            try
            {
                return (ScriptEngine) factory.getClass().getMethod("getScriptEngine", new Class[] { String[].class }).invoke(factory,
                        (Object) nashornArgs.split("\\s+"));
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                LoggingHandler.felog.error("Error Initializing Scripting Engine with Custom Args...  Failing back to Default Args!", e);
            }
        }

        return factory.getScriptEngine();
    }

    public static Compilable getCompilable()
    {
        return (Compilable) getEngine();
    }

    public static synchronized ScriptInstance getScript(File file) throws IOException, ScriptException, CommandException
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

    public static ScriptInstance getScript(String uri) throws IOException, ScriptException, CommandException
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
    public boolean runEventScripts(String key, ICommandSender sender)
    {
        return runEventScripts(key, sender, null);
    }

    @Override
    public boolean runEventScripts(String key, ICommandSender sender, Object additionalData)
    {
        JsICommandSender jsSender = JsICommandSender.get(sender);
        String fnName = "on" + StringUtils.capitalize(key);
        boolean cancelled = false;
        for (ScriptInstance script : scripts.values())
        {
            try
            {
                if (!script.hasGlobalCallFailed(fnName))
                {
                    Object data = null;
                    if (additionalData != null)
                    {
                        data = getEngine().eval("JSON.parse('" + additionalData.toString() + "')");
                    }
                    Object ret = script.tryCallGlobal(fnName, jsSender, data);
                    if (ret instanceof Boolean)
                    {
                        cancelled |= (boolean) ret;
                    }
                }
            }
            catch (ScriptException e)
            {
                e.printStackTrace();
            }
        }
        return cancelled;
    }

}
