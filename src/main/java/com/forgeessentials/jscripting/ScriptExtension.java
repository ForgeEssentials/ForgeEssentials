package com.forgeessentials.jscripting;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * One instance of this class called exactly "ScriptExtensionRoot" <b>must</b> be at the
 * root of every extension package in order to correctly generate tsd files for extensions.
 */
public interface ScriptExtension
{

    void initEngine(ScriptEngine engine, ScriptInstance script) throws ScriptException;

    void serverStarted();

    void serverStopped();

}
