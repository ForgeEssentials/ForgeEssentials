package com.forgeessentials.commands.util;

import com.forgeessentials.api.IPacketAnalyzer;
import com.forgeessentials.commands.CommandVanish;
import com.forgeessentials.core.misc.packetInspector.PacketAnalyzerRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.network.packet.*;

/**
 * Used for vanish
 *
 * @author Dries007
 */
public class PacketAnalyzerCmd implements IPacketAnalyzer {
    public PacketAnalyzerCmd()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }
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
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId))
            {
                return new Packet29DestroyEntity(newp.entityId);
            }
        }
        else if (packet instanceof Packet19EntityAction)
        {
            Packet19EntityAction newp = (Packet19EntityAction) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId))
            {
                return new Packet29DestroyEntity(newp.entityId);
            }
        }
        else if (packet instanceof Packet18Animation)
        {
            Packet18Animation newp = (Packet18Animation) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId))
            {
                return new Packet29DestroyEntity(newp.entityId);
            }
        }
        else if (packet instanceof Packet28EntityVelocity)
        {
            Packet28EntityVelocity newp = (Packet28EntityVelocity) packet;
            if (CommandVanish.vanishedPlayers.contains((Integer) newp.entityId))
            {
                return new Packet29DestroyEntity(newp.entityId);
            }
        }

        return packet;
    }
}