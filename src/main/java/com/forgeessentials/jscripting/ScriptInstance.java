package com.forgeessentials.jscripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptException;

public class ScriptInstance
{

    private File file;

    private long lastModified;

    private CompiledScript script;

    private Invocable invocable;

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
            this.script = ModuleJScripting.getCompiler().compile(reader);
            this.invocable = (Invocable) script.getEngine();

            // Initialization of module environment
            script.getEngine().eval("var exports = {};");
            script.eval();
            // script.getEngine().get("exports")

            this.lastModified = file.lastModified();
        }
    }

    public Object call(String fn, Object... args) throws NoSuchMethodException, ScriptException
    {
        return this.invocable.invokeFunction(fn, args);
    }

    public File getFile()
    {
        return file;
    }

    public void checkIfModified() throws IOException, FileNotFoundException, ScriptException
    {
        if (file.exists() && file.lastModified() != lastModified)
            compileScript();
    }

}
