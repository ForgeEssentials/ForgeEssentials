package com.forgeessentials.auth;

import java.util.UUID;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.network.Packet6AuthLogin;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;

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