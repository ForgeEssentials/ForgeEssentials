package com.forgeessentials.commons.network.packets;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.handler.ReachDistanceHandler;
import com.forgeessentials.commons.network.IFEPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet2Reach implements IFEPacket
{

    public float distance;

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
		Minecraft instance = Minecraft.getInstance();
		if (instance.player != null) {
			ReachDistanceHandler.setReachDistance(distance);
		}
		ForgeEssentialsClient.feclientlog.info("Recieved reach distance from server.");
		
	}

}
