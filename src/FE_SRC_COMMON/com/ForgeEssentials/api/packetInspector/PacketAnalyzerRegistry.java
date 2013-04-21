package com.ForgeEssentials.api.packetInspector;

import net.minecraft.network.packet.Packet;

import com.ForgeEssentials.core.preloader.asm.FEPacketAnalyzer;
import com.google.common.collect.ArrayListMultimap;
/**
 * This is not guaranteed to work!!
 * If the ASM doesn't work properly, it might not get called.
 * 
 * Make sure to include null checking everywhere!
 * You can return null to cancel packets.
 * 
 * This entire system can cause massive lag if you don't optimize your code!
 * 
 * @author Dries007
 *
 */
public class PacketAnalyzerRegistry
{   
    private static ArrayListMultimap<Integer, IPacketAnalyzer> map = ArrayListMultimap.create();
    
    /**
     * Use this method to make sure all patches (ASM) have been applied.
     * If you call this before the server/client is fully loaded (and joined a server in case of client) it will yield false results.
     * @return
     */
    public static boolean allPatchesApplied()
    {
        return FEPacketAnalyzer.MemoryConnection_addToSendQueue && FEPacketAnalyzer.MemoryConnection_processOrCachePacket && FEPacketAnalyzer.TcpConnection_addToSendQueue && FEPacketAnalyzer.TcpConnection_readPacket;
    }
    
    /**
     * Use this to register to all packet id's.
     * @param analyzer
     */
    public static void register(IPacketAnalyzer analyzer)
    {
        for (int i = 0; i < 256; i ++)
            map.put(i, analyzer);
    }
    
    /**
     * Use this to register to certain packet id's only.
     * @param analyzer
     * @param ids
     */
    public static void register(IPacketAnalyzer analyzer, int[] ids)
    {
        for (int i : ids)
            map.put(i, analyzer);
    }
    
    /**
     * Don't ever call this code. It is used by ASM
     * @param packet
     * @return
     */
    public static Packet handleIncoming(Packet packet)
    {
        for (IPacketAnalyzer analyzer : map.get(packet.getPacketId()))
        {
            packet = analyzer.analyzeIncoming(packet);
            if (packet == null) return null;
        }
        
        return packet;
    }
    
    /**
     * Don't ever call this code. It is used by ASM
     * @param packet
     * @return
     */
    public static Packet handleOutgoing(Packet packet)
    {
        for (IPacketAnalyzer analyzer : map.get(packet.getPacketId()))
        {
            packet = analyzer.analyzeOutgoing(packet);
            if (packet == null) return null;
        }
        
        return packet;
    }
}
