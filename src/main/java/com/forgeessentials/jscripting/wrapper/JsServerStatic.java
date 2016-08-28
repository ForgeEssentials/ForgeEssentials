package com.forgeessentials.jscripting.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import javax.script.Invocable;
import javax.script.ScriptException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.RunLaterTimerTask;
import com.forgeessentials.jscripting.ScriptInstance;
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
        ChatOutputHandler.chatConfirmation(MinecraftServer.getServer(), message);
    }

    public void chatNotification(String message)
    {
        ChatOutputHandler.chatNotification(MinecraftServer.getServer(), message);
    }

    public void chatError(String message)
    {
        ChatOutputHandler.chatError(MinecraftServer.getServer(), message);
    }

    public void chatWarning(String message)
    {
        ChatOutputHandler.chatWarning(MinecraftServer.getServer(), message);
    }

    private Map<Integer, TimerTask> tasks = new HashMap<>();

    private int registerTask(TimerTask task)
    {
        int id = (int) Math.round(Math.random() * Integer.MAX_VALUE);
        while (tasks.containsKey(id))
            id = (int) Math.round(Math.random() * Integer.MAX_VALUE);
        tasks.put(id, task);
        return id;
    }

    public int setTimeout(Object fn, long timeout, Object... args) throws NoSuchMethodException, ScriptException
    {
        TimerTask task = new RunLaterTimerTask(new CallScriptMethodTask(script.getInvocable(), fn, args));
        TaskRegistry.schedule(task, timeout);
        return registerTask(task);
    }

    public int setInterval(Object fn, long timeout, Object... args)
    {
        TimerTask task = new RunLaterTimerTask(new CallScriptMethodTask(script.getInvocable(), fn, args));
        TaskRegistry.scheduleRepeated(task, timeout);
        return registerTask(task);
    }

    public void clearTimeout(int id)
    {
        TimerTask task = tasks.remove(id);
        if (task != null)
            TaskRegistry.remove(task);
    }

    public void clearInterval(int id)
    {
        clearTimeout(id);
    }

    public static class CallScriptMethodTask implements Runnable
    {

        private final Object fn;

        private Object[] args;

        private Invocable engine;

        public CallScriptMethodTask(Invocable engine, Object fn, Object... args)
        {
            this.engine = engine;
            this.fn = fn;
            this.args = args;
        }

        @Override
        public void run()
        {
            try
            {
                engine.invokeMethod(fn, "call", args);
            }
            catch (NoSuchMethodException | ScriptException e)
            {
                System.err.println("Error calling script callback");
                e.printStackTrace();
            }
        }
    }
    
}
