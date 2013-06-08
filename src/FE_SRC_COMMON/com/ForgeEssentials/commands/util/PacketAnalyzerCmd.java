package com.ForgeEssentials.commands.util;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet30Entity;

import com.ForgeEssentials.api.IPacketAnalyzer;
import com.ForgeEssentials.commands.CommandVanish;
import com.ForgeEssentials.core.misc.packetInspector.PacketAnalyzerRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Used for vanish
 * @author Dries007
 *
 */
public class PacketAnalyzerCmd implements IPacketAnalyzer
{
    public PacketAnalyzerCmd()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        /*
         * We register all IDs because we need a list of packets that extend 30 gets extended
         */
        PacketAnalyzerRegistry.register(this);
    }
    
    @Override
    public Packet analyzeIncoming(Packet packet)
    {
        return packet;
    }

    @Override
    public Packet analyzeOutgoing(Packet packet)
    {
        /*
         * Vanish
         */
        if (packet instanceof Packet30Entity)
        {
            Packet30Entity newp = (Packet30Entity) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId)) return new Packet29DestroyEntity(newp.entityId);
        }
        else if (packet instanceof Packet19EntityAction)
        {
            Packet19EntityAction newp = (Packet19EntityAction) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId)) return new Packet29DestroyEntity(newp.entityId);
        }
        else if (packet instanceof Packet18Animation)
        {
            Packet18Animation newp = (Packet18Animation) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId)) return new Packet29DestroyEntity(newp.entityId);
        }
        else if (packet instanceof Packet28EntityVelocity)
        {
            Packet28EntityVelocity newp = (Packet28EntityVelocity) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId)) return new Packet29DestroyEntity(newp.entityId);
        }
        
        
        return packet;
    }
}