package com.forgeessentials.commons.network;

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
    /**
     * FE SimpleChannel protocol version.
     */
    private static final String PROTOCOL_VERSION = "FE1";

    /**
     * FE SimpleChannel instance field and generator.
     */
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("forgeessentials", "fe-network"),
        () -> PROTOCOL_VERSION,
        NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION),
        NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION)
    );

    /**
     * Registered network massages.
     */
    private static Set<Integer> registeredMessages = new HashSet<>();

	/**
	 * Register a network packet.<br> Registers a packet that will be sent to the server from the client.
	 */
    public static <MSG extends IFEPacket> void registerClientToServer(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
		registerMessage(index, type, decoder, NetworkDirection.PLAY_TO_SERVER);
	}

    /**
	 * Register a network packet.<br> Registers a packet that will be sent to the client from the server.
	 */
	public static <MSG extends IFEPacket> void registerServerToClient(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
		registerMessage(index, type, decoder, NetworkDirection.PLAY_TO_CLIENT);
	}

	/**
     * Register a network packet.<br> Registers a packet that can be sent from both the client or the server.
     */
    public static <MSG extends IFEPacket> void registerBiDirectional(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder) {
        registerMessage(index, type, decoder, NetworkDirection.PLAY_TO_SERVER);
        registerMessage(index, type, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

	/**
	 * INTERNAL METHOD, DO NOT CALL.
	 */
	private static <MSG extends IFEPacket> void registerMessage(int index, Class<MSG> type, Function<PacketBuffer, MSG> decoder, NetworkDirection networkDirection) {
		if(!registeredMessages.contains(index)) {
	        System.out.println("Registering Network Message id:"+Integer.toString(index)+", Class:"+type.getName());
	        INSTANCE.registerMessage(index, type, IFEPacket::encode, decoder, IFEPacket::handle, Optional.of(networkDirection));
			registeredMessages.add(index);
		}
	}
	/**
	 * Sends a packet to the server.<br> Must be called Client side.
	 */
	public static <MSG extends IFEPacket> void sendToServer(MSG msg) {
	    INSTANCE.sendToServer(msg);
	}

	/**
	 * Send a packet to a specific player.<br> Must be called Server side.
	 */
	public static <MSG extends IFEPacket> void sendTo(MSG msg, ServerPlayerEntity player) {
		if (!(player instanceof FakePlayer)) {
		    INSTANCE.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
