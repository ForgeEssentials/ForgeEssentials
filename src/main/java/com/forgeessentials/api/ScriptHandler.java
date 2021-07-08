package com.forgeessentials.api;

import com.google.gson.JsonObject;
import net.minecraft.command.ICommandSender;

public interface ScriptHandler
{
    /**
     * Call before ServerStarting, so that your scripts with custom keys can be added
     * @param key your custom script key
     */
    void addScriptType(String key);

    /**
     * Call when you want scripts with your custom key to be run.
     * @param key your custom script key
     * @param sender an ICommandSender, if null will default to console
     */
    void runEventScripts(String key, ICommandSender sender);

    /**
     * Call when you want scripts with your custom key to be run.
     * @param key your custom script key
     * @param sender an ICommandSender, if null will default to console
     */
    void runEventScripts(String key, ICommandSender sender, Object additionalData);
}
