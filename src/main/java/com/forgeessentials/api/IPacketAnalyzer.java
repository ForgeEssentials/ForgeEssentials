package com.forgeessentials.api;

import net.minecraft.network.packet.Packet;

/**
 * Register with PacketAnalyzerRegistry.
 * Read the notes in that class!
 *
 * @author Dries007
 */
public interface IPacketAnalyzer {
    /**
     * Analyze incoming packets.
     *
     * @param packet
     * @return null if you want to cancel the packet.
     */
    public Packet analyzeIncoming(Packet packet);

    /**
     * Analyze outgoing packets.
     *
     * @param packet
     * @return null if you want to cancel the packet.
     */
    public Packet analyzeOutgoing(Packet packet);
}
