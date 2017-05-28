package com.forgeessentials.commons.network;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkUtils
{

    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");

    private static Set<Integer> registeredMessages = new HashSet<>();

    public static class NullMessageHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage>
    {
        @Override
        public IMessage onMessage(REQ message, MessageContext ctx)
        {
            return null;
        }

    }

    public static <REQ extends IMessage> void registerMessageProxy(Class<REQ> requestMessageType, int discriminator, Side side, NullMessageHandler<REQ> nmh)
    {
        if (!registeredMessages.contains(discriminator))
            netHandler.registerMessage(nmh, requestMessageType, discriminator, side);
    }

    public static <REQ extends IMessage> void registerMessage(IMessageHandler<REQ, ?> messageHandler, Class<REQ> requestMessageType, int discriminator,
            Side side)
    {
        netHandler.registerMessage(messageHandler, requestMessageType, discriminator, side);
        registeredMessages.add(discriminator);
    }

}
