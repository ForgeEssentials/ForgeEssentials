package com.forgeessentials.jscripting.wrapper.mc;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.Utils;
import com.forgeessentials.util.output.ChatOutputHandler;

/**
 * @tsd.interface Server
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
            server = JsICommandSender.get(srv);
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
     * Registers a new event handler.
     *
     * @tsd.def registerEvent(event: string, handler: (event: mc.event.Event) => void): void;
     */
    public void registerEvent(String event, Object handler) throws ScriptException
    {
        script.registerEventHandler(event, handler);
    }

    /**
     * Broadcast an uncolored message to all players
     */
    public void chat(String message)
    {
        ChatUtil.broadcast(message);
    }

    /**
     * Broadcast a confirmation message to all players
     */
    public void chatConfirm(String message)
    {
        ChatUtil.broadcast(ChatOutputHandler.confirmation(message));
    }

    /**
     * Broadcast a notification message to all players
     */
    public void chatNotification(String message)
    {
        ChatUtil.broadcast(ChatOutputHandler.notification(message));
    }

    /**
     * Broadcast an error message to all players
     */
    public void chatError(String message)
    {
        ChatUtil.broadcast(ChatOutputHandler.error(message));
    }

    /**
     * Broadcast a warning message to all players
     */
    public void chatWarning(String message)
    {
        ChatUtil.broadcast(ChatOutputHandler.warning(message));
    }

    /**
     * Returns the amount of time this player was active on the server in seconds
     */
    public double getTps()
    {
        return Math.min(20, Utils.getTPS());
    }

    /**
     * Time since server start in ms
     */
    public long getUptime()
    {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        return rb.getUptime();
    }

    /**
     * Returns the number of players currently online
     */
    public int getCurrentPlayerCount()
    {
        MinecraftServer server = MinecraftServer.getServer();
        return server == null ? 0 : server.getCurrentPlayerCount();
    }

    /**
     * Returns the total number of unique players that have connected to this server
     */
    public int getUniquePlayerCount()
    {
        return APIRegistry.perms.getServerZone().getKnownPlayers().size();
    }

}
