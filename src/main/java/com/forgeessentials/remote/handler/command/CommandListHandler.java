package com.forgeessentials.remote.handler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;

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
        List<String> commands = new ArrayList<String>();

        @SuppressWarnings("unchecked")
        Set<String> cmds = ServerLifecycleHooks.getCurrentServer().getCommands();// idk

        for (String cmd : cmds)
        {
            commands.add(cmd);
        }

        return new RemoteResponse<List<?>>(RemoteMessageID.COMMAND_COMPLETE, commands);
    }

}
