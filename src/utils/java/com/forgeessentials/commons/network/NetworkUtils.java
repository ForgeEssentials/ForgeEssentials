package com.forgeessentials.commons.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import static cpw.mods.fml.relauncher.Side.SERVER;
import static cpw.mods.fml.relauncher.Side.CLIENT;

public class NetworkUtils
{
    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");

    public static void initClientNullHandlers()
    {
        netHandler.registerMessage(new IMessageHandler<Packet0Handshake, IMessage>()
        {
            @Override
            public IMessage onMessage(Packet0Handshake message, MessageContext ctx)
            {
                return null;
            }
        }, Packet0Handshake.class, 0, SERVER);
    }

    public static void initServerNullHandlers()
    {
        netHandler.registerMessage(new IMessageHandler<Packet1SelectionUpdate, IMessage>()
        {
            @Override
            public IMessage onMessage(Packet1SelectionUpdate message, MessageContext ctx)
            {
                return null;
            }
        }, Packet1SelectionUpdate.class, 1, CLIENT);
        netHandler.registerMessage(new IMessageHandler<Packet5Noclip, IMessage>()
        {
            @Override
            public IMessage onMessage(Packet5Noclip message, MessageContext ctx)
            {
                return null;
            }
        }, Packet5Noclip.class, 5, CLIENT);
        netHandler.registerMessage(new IMessageHandler<Packet6Speed, IMessage>()
        {
            @Override
            public IMessage onMessage(Packet6Speed message, MessageContext ctx)
            {
                return null;
            }
        }, Packet6Speed.class, 6, CLIENT);
        netHandler.registerMessage(new IMessageHandler<Packet7Remote, IMessage>()
        {
            @Override
            public IMessage onMessage(Packet7Remote message, MessageContext ctx)
            {
                return null;
            }
        }, Packet7Remote.class, 7, CLIENT);

    }
}
