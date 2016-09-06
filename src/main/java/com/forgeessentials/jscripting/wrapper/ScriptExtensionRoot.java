package com.forgeessentials.jscripting.wrapper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

import com.forgeessentials.jscripting.ScriptCompiler;
import com.forgeessentials.jscripting.ScriptExtension;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.wrapper.mc.JsServer;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItem;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;
import com.google.common.base.Throwables;

/**
 * @tsd.namespace mc
 */
public class ScriptExtensionRoot implements ScriptExtension
{

    private String INIT_SCRIPT;

    public ScriptExtensionRoot()
    {
        try
        {
            INIT_SCRIPT = IOUtils.toString(ScriptExtensionRoot.class.getResource("init.js"));
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
        }
        ScriptCompiler.registerWrapperClass(Date.class, "");
        ScriptCompiler.registerWrapperClass(Calendar.class, "");
    }

    @Override
    public void initEngine(ScriptEngine engine, ScriptInstance script) throws ScriptException
    {
        engine.put("window", new JsWindow(script));
        engine.put("Server", new JsServer(script));
        engine.put("Block", ScriptCompiler.toNashornClass(JsBlock.class));
        engine.put("Item", ScriptCompiler.toNashornClass(JsItem.class));
        engine.put("World", ScriptCompiler.toNashornClass(JsWorld.class));
        engine.put("localStorage", ScriptCompiler.toNashornClass(JsLocalStorage.class));
        engine.put("Color", ScriptCompiler.toNashornClass(JsFormat.class));

        engine.eval(INIT_SCRIPT);
    }

    @Override
    public void serverStarted()
    {
    }

    @Override
    public void serverStopped()
    {
    }

}
