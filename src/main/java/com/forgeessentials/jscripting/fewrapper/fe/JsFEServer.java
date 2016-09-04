package com.forgeessentials.jscripting.fewrapper.fe;

import javax.script.ScriptException;

import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.command.CommandJScriptCommand;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;

/**
 * @tsd.interface
 */
public class JsFEServer
{

    private ScriptInstance script;

    private JsICommandSender server;

    public JsFEServer(ScriptInstance script)
    {
        this.script = script;
    }

    /**
     * Registers a new command in the game. <br>
     * The processCommand and tabComplete handler can be the same, if the processCommand handler properly checks for args.isTabCompletion.
     *
     * @tsd.def registerCommand(options: CommandOptions): void;
     */
    public void registerCommand(Object options) throws ScriptException
    {
        JsCommandOptions opt = script.getProperties(new JsCommandOptions(), options, JsCommandOptions.class);
        script.registerScriptCommand(new CommandJScriptCommand(script, opt));
    }

}
