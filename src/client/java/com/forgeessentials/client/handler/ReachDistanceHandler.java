package com.forgeessentials.client.handler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.forgeessentials.commons.network.Packet2Reach;

public class ReachDistanceHandler implements IMessageHandler<Packet2Reach, IMessage>
{

    private static float reachDistance = 0;

    @Override
    public IMessage onMessage(Packet2Reach message, MessageContext ctx)
    {
        reachDistance = message.distance;
        return null;
    }

    public static float getReachDistance()
    {
        return reachDistance;
    }

}
