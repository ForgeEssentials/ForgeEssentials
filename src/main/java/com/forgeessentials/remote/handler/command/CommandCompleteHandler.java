package com.forgeessentials.remote.handler.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteCommandSender;
import com.forgeessentials.remote.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.COMMAND_COMPLETE)
public class CommandCompleteHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = CommandHandler.PERM;

    public CommandCompleteHandler()
    {
        super(PERM, String.class);
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<String> request)
    {
        if (request.data == null)
            error("Missing command");

        String[] args = request.data.split(" ");
        String commandName = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);

        ICommand command = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(commandName);
        if (command == null)
            // TODO: Complete command name
            error("Command not found");

        RemoteCommandSender sender = RemoteCommandSender.get(session);
        return new RemoteResponse<List<?>>(RemoteMessageID.COMMAND_COMPLETE, command.addTabCompletionOptions(sender, args, sender.getPosition()));
    }

}
