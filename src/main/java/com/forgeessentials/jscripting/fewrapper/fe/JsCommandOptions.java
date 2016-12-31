package com.forgeessentials.jscripting.fewrapper.fe;

public class JsCommandOptions
{

    public String name;

    /**
     * @tsd.optional
     */
    public String usage;

    /**
     * @tsd.optional
     */
    public String permission;

    /**
     * @tsd.optional
     */
    public boolean opOnly = true;

    /**
     * @tsd.type CommandCallback
     */
    public Object processCommand;

    /**
     * @tsd.optional
     * @tsd.type CommandCallback
     */
    public Object tabComplete;

}
