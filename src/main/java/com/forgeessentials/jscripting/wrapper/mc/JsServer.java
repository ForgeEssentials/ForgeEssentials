package com.forgeessentials.jscripting.wrapper.mc;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.ParseResults;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

/**
 * @tsd.interface Server
 */
public class JsServer
{

    private ScriptInstance script;

    private JsCommandSource server;

    public JsServer(ScriptInstance script)
    {
        this.script = script;
    }

    public JsCommandSource getServer()
    {
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        if (server == null || server.getThat().getServer() != srv)
            server = JsCommandSource.get(srv.createCommandSourceStack());
        return server;
    }

    /**
     * Runs a Minecraft command.<br>
     * Be sure to separate each argument of the command as a single argument to this function. <br>
     * <br>
     * <b>Right:</b> runCommand(sender, 'give', player.getName(), 'minecraft:dirt', 1);<br>
     * <b>Wrong:</b> runCommand(sender, 'give ' + player.getName() + ' minecraft:dirt 1');
     */
    public void runCommand(JsCommandSource sender, String cmd, Object... args)
    {
        doRunCommand(sender, false, cmd, args);
    }

    /**
     * Runs a Minecraft command and ignores any errors it might throw
     */
    public void tryRunCommand(JsCommandSource sender, String cmd, Object... args)
    {
        doRunCommand(sender, true, cmd, args);
    }

    private void doRunCommand(JsCommandSource sender, boolean ignoreErrors, String cmd, Object... args)
    {
        if (sender == null)
            sender = server;

        String[] strArgs = new String[args.length];
        for (int i = 0; i < args.length; i++)
            strArgs[i] = args[i].toString();

        // Join and split again to fix invalid arguments containing spaces
        String cmdLine = StringUtils.join(strArgs, " ");
        cmd = cmd + cmdLine;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        final ParseResults<CommandSourceStack> command = (ParseResults<CommandSourceStack>) server.getCommands().getDispatcher()
                .parse(cmd, server.createCommandSourceStack());
        if (!command.getReader().canRead())
        {
            script.chatError("Command \"" + cmd + "\" not found");
            return;
        }

        try
        {
            server.getCommands().performCommand(sender.getThat(), cmd);
        }
        catch (CommandRuntimeException e)
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
     * Returns the amount of time this player was active on the server in seconds
     */
    public double getTps()
    {
        return Math.min(20, ServerUtil.getTPS());
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
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server == null ? 0 : server.getPlayerCount();
    }

    /**
     * Returns an array of players online
     */
    public String[] getOnlinePlayers()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
        {
            return new String[] {};
        }
        else
        {
            return server.getPlayerNames();
        }
    }

    /**
     * Returns the total number of unique players that have connected to this server
     */
    public int getUniquePlayerCount()
    {
        return APIRegistry.perms.getServerZone().getKnownPlayers().size();
    }

    public List<String> getAllPlayers()
    {
        List<String> x = new ArrayList<>();
        for (UserIdent j : APIRegistry.perms.getServerZone().getKnownPlayers())
        {
            x.add(j.getUsername());
        }
        return x;
    }

    public void serverLog(String msg)
    {
        if (msg != null)
        {
            this.getServer().chat(msg);
        }
    }

    public void tellRaw(String msg)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        try
        {
            Component component = Component.Serializer.fromJson(msg);

            for (Player p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
            {
                server.getPlayerList().broadcastMessage(component, ChatType.CHAT, p.getGameProfile().getId());
            }
        }
        catch (JsonParseException jsonparseexception)
        {
            this.chatError("There is an error in your JSON: " + jsonparseexception.getMessage());
        }
    }

}
