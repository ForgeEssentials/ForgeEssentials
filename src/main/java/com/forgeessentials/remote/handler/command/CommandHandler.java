package com.forgeessentials.remote.handler.command;

import java.util.Arrays;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.core.misc.PermissionManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.remote.RemoteCommandSender;
import com.forgeessentials.remote.RemoteMessageID;

@FERemoteHandler(id = RemoteMessageID.COMMAND)
public class CommandHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = PERM_REMOTE + ".command";

    public CommandHandler()
    {
        super(PERM, String.class);
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.ALL, "Allows to run commands remotely");
    }

    @Override
    protected RemoteResponse<?> handleData(final RemoteSession session, final RemoteRequest<String> request)
    {
        if (request.data == null)
            error("Missing command");

        String[] cmdLine = request.data.split(" ");
        String commandName = cmdLine[0];
        final String[] args = Arrays.copyOfRange(cmdLine, 1, cmdLine.length);

        final ICommand command = (ICommand) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().get(commandName);
        if (command == null)
            error(String.format("Command \"/%s\" not found", commandName));

        checkPermission(session, PermissionManager.getCommandPermission(command));

        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    ICommandSender sender;
                    if (session.getUserIdent() != null && session.getUserIdent().hasPlayer())
                        sender = session.getUserIdent().getPlayer();
                    else
                        sender = RemoteCommandSender.get(session);
                    command.execute(FMLCommonHandler.instance().getMinecraftServerInstance(), sender, args);
                    session.trySendMessage(RemoteResponse.success(request));
                }
                catch (CommandException e)
                {
                    session.trySendMessage(RemoteResponse.error(request, e.getMessage()));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    session.trySendMessage(RemoteResponse.error(request, "Exception: " + e.getMessage()));
                }
            }
        });
        return new RemoteResponse.Ignore();
    }

}
