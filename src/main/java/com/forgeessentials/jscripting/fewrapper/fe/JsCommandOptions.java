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
    public Boolean opOnly = true;

    /**
     * @tsd.optional 
     * @tsd.type JsCommandTypeWrapper
     */
    public Object subNodes;
    
    /**
     * @tsd.type CommandCallback
     */
    public Object processCommand;

}
