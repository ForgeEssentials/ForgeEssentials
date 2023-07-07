package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet9AuthRequest;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet9AuthRequestHandler extends Packet9AuthRequest {
	public Packet9AuthRequestHandler(String hash) {
		super(hash);
	}

	public static Packet9AuthRequestHandler decode(PacketBuffer buf) {
		return new Packet9AuthRequestHandler(buf.readUtf());
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		Minecraft mc = Minecraft.getInstance();
		ForgeEssentialsClient.authDatabase.setKey(mc.getCurrentServer().ip, hash);
		context.setPacketHandled(true);
	}
}