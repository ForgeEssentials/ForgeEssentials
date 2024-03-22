package com.forgeessentials.auth;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.network.packets.Packet08AuthReply;
import com.forgeessentials.util.events.player.PlayerAuthLoginEvent;
import com.forgeessentials.util.events.player.PlayerAuthLoginEvent.Success.Source;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class AuthNetHandler extends Packet08AuthReply
{
    public AuthNetHandler(String hash)
    {
        super(hash);
    }

    public static AuthNetHandler decode(FriendlyByteBuf buf)
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
                    ModuleAuth.authenticate(context.getSender().getGameProfile().getId());
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