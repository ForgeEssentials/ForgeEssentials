package com.forgeessentials.commons.network;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkUtils
{
    /**
     * FE SimpleChannel protocol version. inc when changing packet data
     */
    private static final String PROTOCOL_VERSION = "FE2";

    /**
     * FE Networking logger
     */
    public static final Logger feletworklog = LogManager.getLogger("FEnetwork");
    /**
     * FE SimpleChannel instance field and generator.
     */
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("forgeessentials", "fe-network"), () -> PROTOCOL_VERSION,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION), NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION));

    public static void init()
    {
    }

    /**
     * Registered network massages.
     */
    private static Set<String> registeredMessages = new HashSet<>();

    /**
     * Register a network packet.<br>
     * Registers a packet that will be sent to the server from the client.
     */
    public static <MSG extends IFEPacket> void registerClientToServer(int index, Class<MSG> type,
            BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
            BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler)
    {
        registerMessage(index, type, encoder, decoder, handler, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * Register a network packet.<br>
     * Registers a packet that will be sent to the client from the server.
     */
    public static <MSG extends IFEPacket> void registerServerToClient(int index, Class<MSG> type,
            BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
            BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler)
    {
        registerMessage(index, type, encoder, decoder, handler, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * INTERNAL METHOD, DO NOT CALL.
     */
    private static <MSG extends IFEPacket> void registerMessage(int index, Class<MSG> type,
            BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
            BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler, NetworkDirection networkDirection)
    {
        if (registeredMessages.contains(index + networkDirection.toString()))
        {
            feletworklog.error("Tried registering Network Message id:" + Integer.toString(index) + ", Class:"
                    + type.getSimpleName() + ", Direction:" + networkDirection.toString() + " Twice!");
            return;
        }
        else
        {
            feletworklog.info("Registering Network Message id:" + Integer.toString(index) + ", Class:"
                    + type.getSimpleName() + ", Direction:" + networkDirection.toString());
            INSTANCE.messageBuilder(type, index, networkDirection).decoder(decoder).encoder(encoder).consumer(handler)
                    .add();
            // INSTANCE.registerMessage(index, type, encoder, decoder, IFEPacket::handler,
            // Optional.of(networkDirection));
            registeredMessages.add(index + networkDirection.toString());
        }
    }

    /**
     * Sends a packet to the server.<br>
     * Must be called Client side.
     */
    public static <MSG extends IFEPacket> void sendToServer(MSG msg)
    {
        INSTANCE.sendToServer(msg);
    }

    /**
     * Send a packet to a specific player.<br>
     * Must be called Server side.
     */
    public static <MSG extends IFEPacket> void sendTo(MSG msg, ServerPlayerEntity player)
    {
        if (!(player instanceof FakePlayer))
        {
            INSTANCE.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void handleGetLog(IFEPacket packet) {
        feletworklog.debug("Recieved "+packet.getClass().getSimpleName());
    }

    public static void handleNotHandled(IFEPacket packet) {
        feletworklog.warn(packet.getClass().getSimpleName()+" was not handled properly");
    }
}
