package com.forgeessentialsclient.handler;


import com.forgeessentialsclient.ForgeEssentialsClient;
import com.forgeessentialsclient.utils.commons.network.packets.Packet2Reach;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ReachDistanceHandler extends Packet2Reach
{

    private static float reachDistance = 0;

    public static float getReachDistance()
    {
        return reachDistance;
    }
    public static void setReachDistance(float dis)
    {
        if(dis<0) {dis = Math.abs(dis);}
        reachDistance = dis;
    }
    @Override
	public void handle(Context context) {
		Minecraft instance = Minecraft.getInstance();
		if (instance.player != null) {
			Packet2Reach packet2Reach = new Packet2Reach();
			reachDistance = packet2Reach.distance;
		}
		ForgeEssentialsClient.feclientlog.info("Recieved reach distance from server : "+reachDistance);
		
	}
}
