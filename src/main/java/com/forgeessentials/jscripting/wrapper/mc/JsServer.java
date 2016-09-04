package com.forgeessentials.jscripting.wrapper.mc;

import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.util.output.ChatOutputHandler;

/**
 * @tsd.interface
 */
public class JsServer
{

    private ScriptInstance script;

    private JsICommandSender server;

    public JsServer(ScriptInstance script)
    {
        this.script = script;
    }

    public JsICommandSender getServer()
    {
        MinecraftServer srv = MinecraftServer.getServer();
        if (server == null || server.getThat() != srv)
            server = srv == null ? null : new JsICommandSender(srv);
        return server;
    }

    /**
     * Runs a Minecraft command.<br>
     * Be sure to separate each argument of the command as a single argument to this function. <br>
     * <br>
     * <b>Right:</b> runCommand(sender, 'give', player.getName(), 'minecraft:dirt', 1);<br>
     * <b>Wrong:</b> runCommand(sender, 'give ' + player.getName() + ' minecraft:dirt 1');
     */
    public void runCommand(JsICommandSender sender, String cmd, Object... args)
    {
        doRunCommand(sender, false, cmd, args);
    }

    /**
     * Runs a Minecraft command and ignores any errors it might throw
     */
    public void tryRunCommand(JsICommandSender sender, String cmd, Object... args)
    {
        doRunCommand(sender, true, cmd, args);
    }

    private void doRunCommand(JsICommandSender sender, boolean ignoreErrors, String cmd, Object... args)
    {
        if (sender == null)
            sender = server;

        ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmd);
        if (mcCommand == null)
        {
            script.chatError("Command \"" + cmd + "\" not found");
            return;
        }

        String[] strArgs = new String[args.length];
        for (int i = 0; i < args.length; i++)
            strArgs[i] = args[i].toString();

        // Join and split again to fix invalid arguments containing spaces
        String cmdLine = StringUtils.join(strArgs, " ");
        strArgs = cmdLine.split(" ");

        try
        {
            mcCommand.processCommand(sender.getThat(), strArgs);
        }
        catch (CommandException e)
        {
            if (!ignoreErrors)
                script.chatError(e.getMessage());
        }
    }

    /**
     * Broadcast an uncolored message to all players
     */
    public void chat(String message)
    {
        ChatOutputHandler.broadcast(message);
    }

    /**
     * Broadcast a confirmation message to all players
     */
    public void chatConfirm(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.confirmation(message));
    }

    /**
     * Broadcast a notification message to all players
     */
    public void chatNotification(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.notification(message));
    }

    /**
     * Broadcast an error message to all players
     */
    public void chatError(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.error(message));
    }

    /**
     * Broadcast a warning message to all players
     */
    public void chatWarning(String message)
    {
        ChatOutputHandler.broadcast(ChatOutputHandler.warning(message));
    }

    /**
     * Registers a new event handler.
     *
     * @tsd.def registerEvent(event: string, handler: (event: mc.event.Event) => void): void;
     */
    public void registerEvent(String event, Object handler) throws ScriptException
    {
        script.registerEventHandler(event, handler);
    }

}
