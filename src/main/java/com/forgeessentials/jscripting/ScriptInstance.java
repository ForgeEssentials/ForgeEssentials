package com.forgeessentials.jscripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.minecraft.command.CommandException;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.wrapper.JsBlockStatic;
import com.forgeessentials.jscripting.wrapper.JsCommandArgs;
import com.forgeessentials.jscripting.wrapper.JsItemStatic;
import com.forgeessentials.jscripting.wrapper.JsServerStatic;
import com.forgeessentials.jscripting.wrapper.JsWorldStatic;
import com.forgeessentials.util.CommandParserArgs;

public class ScriptInstance
{

    private File file;

    private long lastModified;

    private CompiledScript script;

    private Invocable invocable;

    private Set<String> illegalFunctions = new HashSet<>();

    public ScriptInstance(File file) throws IOException, ScriptException
    {
        if (!file.exists())
            throw new IllegalArgumentException("file");

        this.file = file;
        compileScript();
    }

    protected void compileScript() throws IOException, FileNotFoundException, ScriptException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            // Load and compile script
            script = ModuleJScripting.getCompilable().compile(reader);

            // Initialization of module environment
            script.getEngine().put("Server", new JsServerStatic(this));
            script.getEngine().put("Block", new JsBlockStatic());
            script.getEngine().put("Item", new JsItemStatic());
            script.getEngine().put("World", new JsWorldStatic());
            script.getEngine().eval("" +
                    "var exports = {};" +
                    // NBT constants
                    "var NBT_BYTE = 'b:';" +
                    "var NBT_SHORT = 's:';" +
                    "var NBT_INT = 'i:';" +
                    "var NBT_LONG = 'l:';" +
                    "var NBT_FLOAT = 'f:';" +
                    "var NBT_DOUBLE = 'd:';" +
                    "var NBT_BYTE_ARRAY = 'B:';" +
                    "var NBT_STRING = 'S:';" +
                    "var NBT_COMPOUND = 'c:';" +
                    "var NBT_INT_ARRAY = 'I:';" +
                    // timeouts
                    "function setTimeout(fn, t, args) { return Server.setTimeout(fn, t, args); };" +
                    "function setInterval(fn, t, args) { return Server.setInterval(fn, t, args); };" +
                    "function clearTimeout(id) { return Server.clearTimeout(id); };" +
                    "function clearInterval(id) { return Server.clearInterval(id); };" +
                    // NBT handling
                    "function getNbt(e) { return JSON.parse(e._getNbt()); }" +
                    "function setNbt(e, d) { e._setNbt(JSON.stringify(d)); }" +
                    "" +
                    "");

            // Start script
            script.eval();
            // script.getEngine().get("exports")

            invocable = (Invocable) script.getEngine();
            illegalFunctions.clear();
            lastModified = file.lastModified();
        }
    }

    public Object call(String fn, Object... args) throws NoSuchMethodException, ScriptException
    {
        try
        {
            return this.invocable.invokeFunction(fn, args);
        }
        catch (Exception e)
        {
            illegalFunctions.add(fn);
            throw e;
        }
    }

    public Object tryCall(String fn, Object... args) throws ScriptException
    {
        try
        {
            return this.invocable.invokeFunction(fn, args);
        }
        catch (NoSuchMethodException e)
        {
            illegalFunctions.add(fn);
            return null;
        }
    }

    public boolean illegalFunction(String fnName)
    {
        return illegalFunctions.contains(fnName);
    }

    public void checkIfModified() throws IOException, FileNotFoundException, ScriptException
    {
        if (file.exists() && file.lastModified() != lastModified)
            compileScript();
    }

    public File getFile()
    {
        return file;
    }

    public ScriptEngine getEngine()
    {
        return script.getEngine();
    }

    public Invocable getInvocable()
    {
        return (Invocable) script.getEngine();
    }

    public void runCommand(CommandParserArgs arguments) throws CommandException
    {
        try
        {
            call(arguments.isTabCompletion ? "tabComplete" : "processCommand", new JsCommandArgs(arguments));
        }
        catch (CommandException e)
        {
            throw e;
        }
        catch (NoSuchMethodException e)
        {
            if (!arguments.isTabCompletion)
                throw new TranslatedCommandException("Script missing processCommand function.");
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
            throw new TranslatedCommandException("Error in script: %s", e.getMessage());
        }
    }

}
