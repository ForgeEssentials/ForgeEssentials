package com.forgeessentials.auth;

import java.util.UUID;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;
import com.forgeessentials.util.events.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.PlayerAuthLoginEvent.Success.Source;

public class AuthNetHandler extends Packet6AuthLogin
{
    @Override
    public void handle(Context context)
    {

        if (!ModuleAuth.allowAutoLogin)
            // return null;
            switch (mode)
            {
            case 1:
                if (!hash.isEmpty())
                {
                    if (PasswordManager.hasSession(UserIdent.get(context.getSender()).getUuid(), UUID.fromString(hash)))
                    {
                        ModuleAuth.authenticate(context.getSender().getUUID());
                        APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success(context.getSender(), Source.AUTOLOGIN));
                    }
                }
                break;
            default:
                break;

            }
        context.setPacketHandled(true);
    }
}