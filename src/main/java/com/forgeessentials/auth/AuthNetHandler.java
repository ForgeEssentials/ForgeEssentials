package com.forgeessentials.auth;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.network.Packet6AuthLogin;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class AuthNetHandler implements IMessageHandler<Packet6AuthLogin, IMessage>
{
    @Override
    public IMessage onMessage(Packet6AuthLogin message, MessageContext ctx)
    {

        switch(message.mode)
        {
        case 1:
            if (message.hash != "")
            {
                if (PasswordManager.hasSession(UserIdent.get(ctx.getServerHandler().playerEntity).getUuid(), UUID.fromString(message.hash)))
                {
                    ModuleAuth.authenticate(UserIdent.get(ctx.getServerHandler().playerEntity).getUuid());
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success(ctx.getServerHandler().playerEntity, Source.AUTOLOGIN));
                }
            }
            break;
        default:
            break;

        }
        return null;
    }
}