package com.forgeessentials.auth;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.network.Packet6AuthLogin;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.FEPlayerEvent.PlayerAuthLoginEvent.Source;

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
                if (PasswordManager.hasSession(ctx.getServerHandler().playerEntity.getUniqueID(), UUID.fromString(message.hash)))
                {
                    ModuleAuth.authenticate(ctx.getServerHandler().playerEntity.getUniqueID());
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent(ctx.getServerHandler().playerEntity, Source.AUTOLOGIN));
                }
            }
            break;
        default:
            break;

        }
        return null;
    }
}