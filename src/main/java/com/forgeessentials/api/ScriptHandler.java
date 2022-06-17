package com.forgeessentials.api;

import net.minecraft.command.CommandSource;

public interface ScriptHandler
{
    /**
     * Call before ServerStarting, so that your scripts with custom keys can be added
     * @param key your custom script key
     */
    void addScriptType(String key);

    /**
     * Call when you want scripts with your custom key to be run.
     *
     * @param key    your custom script key
     * @param sender an ICommandSender, if null will default to console
     */
    boolean runEventScripts(String key, CommandSource sender);

    /**
     * Call when you want scripts with your custom key to be run.
     *
     * @param key    your custom script key
     * @param sender an ICommandSender, if null will default to console
     */
    boolean runEventScripts(String key, CommandSource sender, Object additionalData);
}
