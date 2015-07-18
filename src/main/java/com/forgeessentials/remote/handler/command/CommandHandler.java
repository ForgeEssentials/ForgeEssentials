package com.forgeessentials.remote.handler.command;

import java.util.Arrays;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteCommandSender;
import com.forgeessentials.remote.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.COMMAND)
public class CommandHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = PERM_REMOTE + ".command";

    public CommandHandler()
    {
        super(PERM, String.class);
        APIRegistry.perms.registerPermission(PERM, PermissionLevel.TRUE, "Allows to run commands remotely");
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<String> request)
    {
        if (request.data == null)
            error("Missing command");

        String[] args = request.data.split(" ");
        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        ICommand command = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(commandName);
        if (command == null)
            error(String.format("Command \"%s\" not found", commandName));

        RemoteCommandSender sender = new RemoteCommandSender(session);

        if (!PermissionManager.checkPermission(sender, command))
            error(RemoteHandler.MSG_NO_PERMISSION);

        try
        {
            command.processCommand(sender, args);
        }
        catch (CommandException e)
        {
            error(e.getMessage());
        }
        catch (Exception e)
        {
            error("Exception: " + e.getMessage());
        }
        return success(request);
    }

}
