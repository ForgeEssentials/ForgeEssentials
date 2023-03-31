package com.forgeessentialsclient.utils.commons.network;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;


import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class NetworkUtils
{
	public static final String PROTOCOL_VERSION = "FE1";
	public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation("forgeessentials", "fe-network"))
			.clientAcceptedVersions(NetworkRegistry.ABSENT::equals)
			.serverAcceptedVersions(NetworkRegistry.ABSENT::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
    private static Set<Integer> registeredMessages = new HashSet<>();

    
    
    
    
/*
    public static class NullMessageHandler<MSG extends SimpleChannel> implements IMessageHandler<MSG, SimpleChannel>
    {
        @Override
        public SimpleChannel onMessage(MSG message, MessageContext ctx)
        {
            return null;
        }

    }

    public static <MSG extends SimpleChannel> void registerMessageProxy(Class<MSG> requestMessageType, int discriminator, Dist side, NullMessageHandler<MSG> nmh)
    {
        if (!registeredMessages.contains(discriminator))
            netHandler.registerMessage(nmh, requestMessageType, discriminator, side);
    }*/
    public static <MSG extends IFEPacket> void registerClientToServer(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
		registerMessage(index, type, decoder, NetworkDirection.PLAY_TO_SERVER);
	}

	public static <MSG extends IFEPacket> void registerServerToClient(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
		registerMessage(index, type, decoder, NetworkDirection.PLAY_TO_CLIENT);
	}

	private static <MSG extends IFEPacket> void registerMessage(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder, NetworkDirection networkDirection) {
		HANDLER.registerMessage(index, type, IFEPacket::encode, decoder, IFEPacket::handle, Optional.of(networkDirection));
		registeredMessages.add(index);
	}
	/**
	 * Sends a packet to the server.<br> Must be called Client side.
	 */
	public static <MSG extends IFEPacket> void sendToServer(MSG msg) {
		HANDLER.sendToServer(msg);
	}

	/**
	 * Send a packet to a specific player.<br> Must be called Server side.
	 */
	public static <MSG extends IFEPacket> void sendTo(MSG msg, ServerPlayerEntity player) {
		if (!(player instanceof FakePlayer)) {
			HANDLER.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
