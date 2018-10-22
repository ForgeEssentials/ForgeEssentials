package com.forgeessentials.jscripting.wrapper.mc;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.gson.JsonParseException;

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
        MinecraftServer srv = FMLCommonHandler.instance().getMinecraftServerInstance();
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

        ICommand mcCommand = (ICommand) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().get(cmd);
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
            mcCommand.execute(FMLCommonHandler.instance().getMinecraftServerInstance(), sender.getThat(), strArgs);
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
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return server == null ? 0 : server.getCurrentPlayerCount();
    }
    /**
     * Returns an array of players online
     */
    public String[] getOnlinePlayers()
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null)
        {
        	return new String[] {};
        } else {
        	return server.getOnlinePlayerNames();
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
    public void serverLog(String msg) {
    	if (msg != null)
        {
            this.getServer().chat(msg);
        }
    }
    public void tellRaw(String msg) {
    	MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        try
        {    
        	ITextComponent component = ITextComponent.Serializer.jsonToComponent(msg);
            server.getPlayerList().sendMessage(component);
        }
        catch (JsonParseException jsonparseexception)
        {
            this.chatError("There is an error in your JSON: "+jsonparseexception.getMessage());
        } 
    }

}
