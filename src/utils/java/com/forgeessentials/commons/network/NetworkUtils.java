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

    public static class NullMessageHandler implements IMessageHandler<IMessage, IMessage>
    {

        @Override
        public IMessage onMessage(IMessage message, MessageContext ctx)
        {
            return null;
        }
    }

    public static void initClientNullHandlers()
    {
        netHandler.registerMessage(new NullMessageHandler(), Packet0Handshake.class, 0, SERVER);
    }

    public static void initServerNullHandlers()
    {
        netHandler.registerMessage(new NullMessageHandler(), Packet1SelectionUpdate.class, 1, CLIENT);
        netHandler.registerMessage(new NullMessageHandler(), Packet5Noclip.class, 5, CLIENT);
        netHandler.registerMessage(new NullMessageHandler(), Packet6Speed.class, 6, CLIENT);
        netHandler.registerMessage(new NullMessageHandler(), Packet7Remote.class, 7, CLIENT);

    }
}
