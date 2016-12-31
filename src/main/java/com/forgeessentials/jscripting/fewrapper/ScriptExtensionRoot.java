package com.forgeessentials.jscripting.fewrapper;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

import com.forgeessentials.jscripting.ScriptExtension;
import com.forgeessentials.jscripting.ScriptCompiler;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsAreaShape;
import com.forgeessentials.jscripting.fewrapper.fe.JsFEServer;
import com.forgeessentials.jscripting.fewrapper.fe.JsPermissions;
import com.forgeessentials.jscripting.fewrapper.fe.JsZone;
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
    }

    @Override
    public void initEngine(ScriptEngine engine, ScriptInstance script) throws ScriptException
    {
        engine.put("Permissions", ScriptCompiler.toNashornClass(JsPermissions.class));
        engine.put("PermissionLevel", ScriptCompiler.toNashornClass(JsPermissionLevel.class));
        engine.put("AreaShape", ScriptCompiler.toNashornClass(JsAreaShape.class));
        engine.put("FEServer", new JsFEServer(script));

        engine.eval(INIT_SCRIPT);
    }

    @Override
    public void serverStarted()
    {
    }

    @Override
    public void serverStopped()
    {
        JsPermissions.serverZone = null;
        JsZone.cache.clear();
    }

}
