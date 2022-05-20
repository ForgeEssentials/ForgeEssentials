package com.forgeessentialsclient.utils.commons.network.packets;


import com.forgeessentialsclient.utils.commons.network.IFEPacket;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet2Reach implements IFEPacket
{

    public float distance;
    public Packet2Reach() {}
    public Packet2Reach(float distance)
    {
        this.distance = distance;
    }

    public static Packet2Reach decode(PacketBuffer buf)
    {
    	return new Packet2Reach(buf.readFloat());
    }
    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeFloat(distance);
    }

	@Override
	public void handle(Context context) {
		// TODO Auto-generated method stub
	}
}
