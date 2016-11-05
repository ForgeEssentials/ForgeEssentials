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

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.jscripting.wrapper.ScriptExtensionRoot;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.Utils;

public class ScriptManager
{

    private static final ScriptEngineManager SEM = new ScriptEngineManager(null);

    private static final ScriptManager instance = new ScriptManager();

    public static final String COMMANDS_DIR = "./jscripts/";

    private static boolean _isNashorn;

    private static boolean _isRhino;

    public static ScriptManager instance()
    {
        return instance;
    }

    /**
     * Script cache
     */
    protected Map<File, ScriptInstance> scripts = new HashMap<>();

    /* ------------------------------------------------------------ */

    static {
        System.setProperty("nashorn.args", "-strict --no-java --no-syntax-extensions");
        ScriptEngine engine = SEM.getEngineByName("JavaScript");
        if (engine == null)
            throw new RuntimeException("Your JVM does not support running JavaScript.");
        _isNashorn = engine.getFactory().getEngineName().toLowerCase().contains("nashorn");
        _isRhino = engine.getFactory().getEngineName().toLowerCase().contains("rhino");
    }

    private ScriptManager() {
        // Register default wrapper package
        ScriptCompiler.registerExtension(new ScriptExtensionRoot());
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
        for (Iterator<File> it = FileUtils.iterateFiles(JScriptingMod.moduleDir, new String[] { "js" }, true); it.hasNext(); )
        {
            File file = it.next();
            if (scripts.containsKey(file))
                continue;
            try
            {
                getScript(file);
            }
            catch (CommandException | IOException | ScriptException e)
            {
                String scriptName = file.getName();
                ChatUtil.sendMessage(sender, String.format("FE Script error in %s:", scriptName), EnumChatFormatting.RED);
                ChatUtil.sendMessage(sender, e.getMessage(), EnumChatFormatting.RED);
                Utils.felog.error(String.format("FE Script error in %s: %s", scriptName, e.getMessage()));
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

    public synchronized ScriptInstance getScript(File file) throws IOException, ScriptException
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

    public ScriptInstance getScript(String uri) throws IOException, ScriptException
    {
        File f = new File(JScriptingMod.moduleDir, uri);
        if (!f.exists())
            return null;
        return getScript(f);
    }

    public Collection<ScriptInstance> getScripts()
    {
        return scripts.values();
    }

    public static File getModuleDir()
    {
        return JScriptingMod.moduleDir;
    }

    /* ------------------------------------------------------------ */
    /* Script handling API */

    // @Override
    public void addScriptType(String key)
    {
        String fnName = "on" + StringUtils.capitalize(key);
        try
        {
            new File(JScriptingMod.moduleDir, fnName + ".txt").createNewFile();
        }
        catch (IOException e)
        {
            /* nothing */
        }
    }

    // @Override
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
