package com.forgeessentials.auth;

import java.util.UUID;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;

public class AuthNetHandler extends Packet6AuthLogin
{
    @Override
    public void handle(Context context) {
    {

        if (!ModuleAuth.allowAutoLogin)
            return null;
        switch(message.mode)
        {
        case 1:
            if (!message.hash.isEmpty())
            {
                if (PasswordManager.hasSession(UserIdent.get(ctx.getServerHandler().player).getUuid(), UUID.fromString(message.hash)))
                {
                    ModuleAuth.authenticate(UserIdent.get(ctx.getServerHandler().player).getUuid());
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success(ctx.getServerHandler().player, Source.AUTOLOGIN));
                }
            }
            break;
        default:
            break;

        }
        return null;
    }
}