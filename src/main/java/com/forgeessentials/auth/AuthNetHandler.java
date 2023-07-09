package com.forgeessentials.auth;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.network.packets.Packet8AuthReply;
import com.forgeessentials.util.events.player.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.player.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class AuthNetHandler extends Packet8AuthReply
{
    public AuthNetHandler(String hash)
    {
        super(hash);
    }

    public static AuthNetHandler decode(PacketBuffer buf)
    {
        return new AuthNetHandler(buf.readUtf());
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        if (ModuleAuth.allowAutoLogin)
        {
            if (!hash.isEmpty())
            {
                if (PasswordManager.hasSession(UserIdent.get(context.getSender()).getUuid(), UUID.fromString(hash)))
                {
                    ModuleAuth.authenticate(context.getSender().getUUID());
                    APIRegistry.getFEEventBus().post(new PlayerAuthLoginEvent.Success(context.getSender(), Source.AUTOLOGIN));
                    ChatOutputHandler.chatConfirmation(context.getSender(), "AutoAuth Login Successful.");
                }
                else
                {
                    ChatOutputHandler.chatError(context.getSender(), "Failed to AutoAuth Login, please authenticate and login again for this to go away.");
                }
            }
        }
    }
}