package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;
import com.forgeessentials.commons.network.packets.Packet8AuthReply;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet6AuthLoginHandler extends Packet6AuthLogin {
	public Packet6AuthLoginHandler() {
		super();
	}

	public static Packet6AuthLoginHandler decode(PacketBuffer buf) {
		return new Packet6AuthLoginHandler();
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		// send empty response if the client has disabled this
		if (!ForgeEssentialsClient.allowAuthAutoLogin) {
			NetworkUtils.sendToServer(new Packet8AuthReply(""));
		}
		Minecraft mc = Minecraft.getInstance();
		NetworkUtils.sendToServer(new Packet8AuthReply(ForgeEssentialsClient.authDatabase.getKey(mc.getCurrentServer().ip)));
		context.setPacketHandled(true);
	}
}