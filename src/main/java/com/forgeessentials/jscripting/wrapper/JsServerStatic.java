package com.forgeessentials.jscripting.wrapper;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.util.output.ChatOutputHandler;

public class JsServerStatic
{

    public JsCommandSender server = new JsCommandSender(MinecraftServer.getServer());

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

}
