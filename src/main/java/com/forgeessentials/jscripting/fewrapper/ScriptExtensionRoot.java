package com.forgeessentials.jscripting.fewrapper;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.io.IOUtils;

import com.forgeessentials.jscripting.ScriptCompiler;
import com.forgeessentials.jscripting.ScriptExtension;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.fewrapper.fe.JsAreaShape;
import com.forgeessentials.jscripting.fewrapper.fe.JsFEServer;
import com.forgeessentials.jscripting.fewrapper.fe.JsPermissions;
import com.forgeessentials.jscripting.fewrapper.fe.JsZone;

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
            INIT_SCRIPT = IOUtils.toString(ScriptExtensionRoot.class.getResource("init.js"),"UTF-8");
        }
        catch (IOException e)
        {
        	throw new RuntimeException(e);
        }
    }

    @Override
    public void initEngine(ScriptEngine engine, ScriptInstance script) throws ScriptException
    {
        engine.put("Permissions", ScriptCompiler.toNashornClass(JsPermissions.class));
        engine.put("PermissionLevel", ScriptCompiler.toNashornClass(JsPermissionLevel.class));
        engine.put("AreaShape", ScriptCompiler.toNashornClass(JsAreaShape.class));
        JsFEServer feServer = new JsFEServer(script);
        MinecraftForge.EVENT_BUS.register(feServer);
        engine.put("FEServer", feServer);

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
