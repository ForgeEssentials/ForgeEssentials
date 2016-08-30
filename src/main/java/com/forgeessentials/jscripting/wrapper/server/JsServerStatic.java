package com.forgeessentials.jscripting.wrapper.server;

import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.command.CommandJScriptCommand;
import com.forgeessentials.jscripting.wrapper.JsCommandOptions;
import com.forgeessentials.jscripting.wrapper.JsCommandSender;
import com.forgeessentials.util.output.ChatOutputHandler;

public class JsServerStatic
{

    private ScriptInstance script;

    private JsCommandSender server;

    public JsServerStatic(ScriptInstance script)
    {
        this.script = script;
    }

    public JsCommandSender getServer()
    {
        MinecraftServer srv = MinecraftServer.getServer();
        if (server == null || server.getThat() != srv)
            server = srv == null ? null : new JsCommandSender(srv);
        return server;
    }

    public void runCommand(JsCommandSender sender, String cmd, Object... args) throws CommandException
    {
        if (sender == null)
            sender = server;

        ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
        if (mcCommand == null)
            return;

        try
        {
            String[] strArgs = new String[args.length];
            for (int i = 0; i < args.length; i++)
                strArgs[i] = args[i].toString();

            mcCommand.processCommand(sender.getThat(), strArgs);
        }
        catch (CommandException e)
        {
            // if (!ignoreErrors)
            // throw e;
            // LoggingHandler.felog.info(String.format("Silent script command /%s %s failed: %s", cmd, StringUtils.join(args, " "), e.getMessage()));
            e.printStackTrace();
            throw e;
        }
    }

    public void chatConfirm(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.confirmation(message));
    }

    public void chatNotification(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.notification(message));
    }

    public void chatError(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.error(message));
    }

    public void chatWarning(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.warning(message));
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

    /**
     * Registers a new event handler.
     * 
     * @tsd.def registerEvent(event: string, handler: (event: MC.Event.Event) => void): void;
     */
    public void registerEvent(String event, Object handler) throws ScriptException
    {
        script.registerEventHandler(event, handler);
    }

    /**
     * Set a timeout to call 'handler' after 'timeout' milliseconds.
     * 
     * @tsd.def setTimeout(handler: (...args: any[]) => void, timeout?: any, ...args: any[]): number;
     */
    public int setTimeout(Object fn, long timeout, Object... args)
    {
        return script.setTimeout(fn, timeout, args);
    }

    /**
     * Set a interval to call 'handler' fn repeatedly each 'interval' milliseconds.
     * 
     * @tsd.def setInterval(handler: (...args: any[]) => void, interval?: any, ...args: any[]): number;
     */
    public int setInterval(Object fn, long timeout, Object... args)
    {
        return script.setInterval(fn, timeout, args);
    }

    /**
     * Clear a timeout.
     */
    public void clearTimeout(int handle)
    {
        script.clearTimeout(handle);
    }

    /**
     * Clear an interval.
     */
    public void clearInterval(int handle)
    {
        script.clearTimeout(handle);
    }

}
