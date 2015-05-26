package com.forgeessentials.remote.handler.command;

import java.util.Arrays;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fe.server.CommandHandlerForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.remote.RemoteCommandSender;
import com.forgeessentials.remote.handler.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.COMMAND)
public class CommandHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = PERM_REMOTE + ".command";

    public CommandHandler()
    {
        super(PERM, String.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.TRUE, "Allows to run commands remotely");
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
            error("Command not found");

        RemoteCommandSender sender = new RemoteCommandSender(session);
        
        boolean canUse;
        if (command instanceof ForgeEssentialsCommandBase)
            canUse = ((ForgeEssentialsCommandBase) command).checkCommandPermission(sender);
        else
            canUse = CommandHandlerForge.canUse(command, sender);
        if (!canUse)
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
