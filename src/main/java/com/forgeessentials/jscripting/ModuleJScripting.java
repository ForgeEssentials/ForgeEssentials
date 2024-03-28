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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.ScriptHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.jscripting.command.CommandJScript;
import com.forgeessentials.jscripting.wrapper.JsLocalStorage;
import com.forgeessentials.jscripting.wrapper.mc.JsCommandSource;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

@FEModule(name = "JScripting", parentMod = ForgeEssentials.class, version=ForgeEssentials.CURRENT_MODULE_VERSION)
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

    static
    {
        nashornArgs = System.getProperty("fe.nashorn.args");
        if (nashornArgs == null)
        {
            nashornArgs = DEFAULT_NASHORN_ARGS;
        }
    }

    /**
     * Script cache
     */
    protected static Map<File, ScriptInstance> scripts = new HashMap<>();

    /* ------------------------------------------------------------ */

    public ModuleJScripting()
    {
        APIRegistry.scripts = this;
        ScriptCompiler.registerExtension(new com.forgeessentials.jscripting.wrapper.ScriptExtensionRoot());
        ScriptCompiler.registerExtension(new com.forgeessentials.jscripting.fewrapper.ScriptExtensionRoot());
    }

    public static ModuleJScripting instance()
    {
        return instance;
    }

    @Preconditions
    public static boolean canLoad()
    {
        SEM.registerEngineName("nashorn", new NashornScriptEngineFactory());
        ScriptEngine engine = SEM.getEngineByName("nashorn");
        LoggingHandler.felog.debug(engine.toString());
        if (engine != null && (factory = engine.getFactory()) != null)
        {
            isNashorn = factory.getEngineName().toLowerCase().contains("nashorn");
            isRhino = factory.getEngineName().toLowerCase().contains("rhino");
        }
        return factory != null;
    }

    public void init()
    {
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
    }

    public CommandDispatcher<CommandSourceStack> dispatcher = null;
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandJScript(true), event.getDispatcher());

        dispatcher = event.getDispatcher();
        init();
    }

    private void copyResourceFileIfNotExists(String fileName) throws IOException
    {
        File file = new File(moduleDir, fileName);
        if (!file.exists())
            FileUtils.copyInputStreamToFile(ModuleJScripting.class.getResourceAsStream(fileName), file);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        JsLocalStorage.load();
        loadScripts(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack());
    }

    @Override
    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        unloadScripts();
        JsLocalStorage.save();
    }

    //@SubscribeEvent
    public void reload(ConfigReloadEvent event)
    {
        LoggingHandler.felog.info("Reloading scripts");
        reloadScripts(ServerLifecycleHooks.getCurrentServer().createCommandSourceStack());
    }

    public void reloadScripts(CommandSourceStack sender)
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

    public void loadScripts(CommandSourceStack sender)
    {
        Iterator<File> it;
        try {
            it = FileUtils.iterateFiles(moduleDir, new String[] { "js", "ts" }, true);
        }catch(NullPointerException e) {
            ChatOutputHandler.chatError(sender, "FE error loading all scripts");
            ChatOutputHandler.chatError(sender, "NullPointerException: " + e.getMessage());
            LoggingHandler.felog.error(String.format("FE error loading all scripts: %s", e.getMessage()));
            if (e.getMessage() == null) {
                e.printStackTrace();
            }
            return;
        }
        while (it.hasNext()) {
            File file = it.next();
            String name = file.getName();
            if (!(name.equals("fe.d.ts")|| name.equals("mc.d.ts"))&& name.endsWith("ts"))
            {
                LoggingHandler.felog.warn(
                        "Typescript file: {} found! This file must be transpiled to javascript with the js extension.  This file will be ignored.",
                        name);
                continue;
            }
            if ((name.equals("fe.d.ts")|| name.equals("mc.d.ts")) || scripts.containsKey(file))
                continue;
            try
            {
                getScript(file);
            }
            catch (CommandRuntimeException | IOException | ScriptException e)
            {
                String scriptName = file.getName();
                ChatOutputHandler.chatError(sender, String.format("FE Script error in %s:", scriptName));
                ChatOutputHandler.chatError(sender, e.getMessage());
                LoggingHandler.felog.error(String.format("FE Script error in %s: %s", scriptName, e.getMessage()));
                if (e.getMessage() == null) {
                    e.printStackTrace();
                }
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
                return (ScriptEngine) factory.getClass().getMethod("getScriptEngine", new Class[] { String[].class })
                        .invoke(factory, (Object) nashornArgs.split("\\s+"));
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                LoggingHandler.felog.error(
                        "Error Initializing Scripting Engine with Custom Args...  Failing back to Default Args!", e);
            }
        }

        return factory.getScriptEngine();
    }

    public static Compilable getCompilable()
    {
        return (Compilable) getEngine();
    }

    public static synchronized ScriptInstance getScript(File file)
            throws IOException, ScriptException, CommandRuntimeException
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

    public static ScriptInstance getScript(String uri) throws IOException, ScriptException, CommandRuntimeException
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
    public boolean runEventScripts(String key, CommandSourceStack sender)
    {
        return runEventScripts(key, sender, null);
    }

    @Override
    public boolean runEventScripts(String key, CommandSourceStack sender, Object additionalData)
    {
        JsCommandSource jsSender = JsCommandSource.get(sender);
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
                        data = getEngine().eval("JSON.parse('" + DataManager.toJson(additionalData).replaceAll("\n", "") + "')");
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
