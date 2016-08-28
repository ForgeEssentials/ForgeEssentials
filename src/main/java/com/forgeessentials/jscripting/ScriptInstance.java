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

import com.forgeessentials.jscripting.wrapper.JsBlockStatic;
import com.forgeessentials.jscripting.wrapper.JsItemStatic;
import com.forgeessentials.jscripting.wrapper.JsServerStatic;
import com.forgeessentials.jscripting.wrapper.JsWorldStatic;

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
                    "var setTimeout = function(fn, t, args) { return Server.setTimeout(fn, t, args); };" +
                    "var setInterval = function(fn, t, args) { return Server.setInterval(fn, t, args); };" +
                    "var clearTimeout = function(id) { return Server.clearTimeout(id); };" +
                    "var clearInterval = function(id) { return Server.clearInterval(id); };");

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

}
