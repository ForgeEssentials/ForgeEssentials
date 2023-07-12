package com.forgeessentials.serverNetwork.server.packets;

import com.forgeessentials.serverNetwork.server.packets.bidirectional.CloseSessionPacket;
import com.forgeessentials.serverNetwork.server.packets.clientTypes.ClientPasswordPacket;
import com.forgeessentials.serverNetwork.server.packets.serverTypes.ServerPasswordResponcePacket;

public enum PacketType {
    DATA {
        @Override
        protected Packet decode(byte[] data)
        {
            DataPacket packet = new DataPacket(null).decode(data);
            return packet;
        }
    },
    PASSWORD {
        @Override
        protected Packet decode(byte[] data)
        {
            ClientPasswordPacket packet = new ClientPasswordPacket(null).decode(data);
            return packet;
        }
    }, 
    CLOSE_SESSION {
        @Override
        protected Packet decode(byte[] data)
        {
            CloseSessionPacket packet = new CloseSessionPacket().decode(data);
            return packet;
        }
    }, 
    CUSTOM {
        @Override
        protected Packet decode(byte[] data)
        {
            CustomPacket packet = new CustomPacket(null, 0, false).decode(data);
            return packet;
        }
    }, SERVER_PASSWORD_RESPONSE {
        @Override
        protected Packet decode(byte[] data)
        {
            ServerPasswordResponcePacket packet = new ServerPasswordResponcePacket(false).decode(data);
            return packet;
        }
    };

    protected abstract Packet decode(byte[] data);
    public static Packet decodePacket(PacketType packetType, byte[] data) {
        return packetType.decode(data);
    }
}
