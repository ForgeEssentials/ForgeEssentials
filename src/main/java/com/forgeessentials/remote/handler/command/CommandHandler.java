package com.forgeessentials.remote.handler.command;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.remote.RemoteCommandSender;
import com.forgeessentials.remote.RemoteMessageID;
import com.mojang.brigadier.ParseResults;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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

        String commandName = request.data;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        final ParseResults<CommandSourceStack> command = server.getCommands().getDispatcher()
                .parse(commandName, server.createCommandSourceStack());
        if (!command.getReader().canRead())
            error(String.format("Command \"/%s\" not found", commandName));

        checkPermission(session, command.getReader().getString().substring(1));

        TaskRegistry.runLater(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    CommandSourceStack sender;
                    if (session.getUserIdent() != null && session.getUserIdent().hasPlayer())
                        sender = session.getUserIdent().getPlayer().createCommandSourceStack();
                    else
                        sender = RemoteCommandSender.get(session).createCommandSourceStack();
                    server.getCommands().performCommand(sender, commandName);
                    session.trySendMessage(RemoteResponse.success(request));
                }
                catch (CommandRuntimeException e)
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
