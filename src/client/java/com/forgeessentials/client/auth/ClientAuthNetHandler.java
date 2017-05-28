package com.forgeessentials.client.auth;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.commons.network.Packet6AuthLogin;

public class ClientAuthNetHandler implements IMessageHandler<Packet6AuthLogin, IMessage>
{
    @Override
    public IMessage onMessage(Packet6AuthLogin message, MessageContext ctx)
    {
        // send empty response if the client has disabled this
        if (!ClientProxy.allowAuthAutoLogin)
            return new Packet6AuthLogin(1, "");

        AuthAutoLogin.KEYSTORE = AuthAutoLogin.load();
        switch (message.mode)
        {
        case 0:
            return new Packet6AuthLogin(1, AuthAutoLogin.getKey(Minecraft.getMinecraft().getCurrentServerData().serverIP));
        case 2:
            AuthAutoLogin.setKey(Minecraft.getMinecraft().getCurrentServerData().serverIP, message.hash);
            break;
        default:
            break;
        }
        return null;
    }
}