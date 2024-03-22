package com.forgeessentials.remote.handler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

@FERemoteHandler(id = RemoteMessageID.COMMAND_LIST)
public class CommandListHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = CommandHandler.PERM;

    public CommandListHandler()
    {
        super(PERM, String.class);
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<String> request)
    {

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Map<CommandNode<CommandSourceStack>, String> map = server.getCommands().getDispatcher()
                .getSmartUsage(server.getCommands().getDispatcher().getRoot(), server.createCommandSourceStack());

        List<String> commands = new ArrayList<>(map.values());

        return new RemoteResponse<List<?>>(RemoteMessageID.COMMAND_COMPLETE, commands);
    }

}
