package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet8AuthReply implements IFEPacket {
	/*
	 * reply from client with hash (empty if client does not have hash) 
	 */
	public String hash;

	public Packet8AuthReply(String hash) {
		this.hash = hash;
	}

	public static Packet8AuthReply decode(PacketBuffer buf) {
		return new Packet8AuthReply(buf.readUtf());
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeUtf(hash);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		NetworkUtils.feletworklog.warn("Packet8AuthReply was not handled properly");
	}

	public static void handler(final Packet8AuthReply message, Supplier<NetworkEvent.Context> ctx) {
		NetworkUtils.feletworklog.info("Recieved Packet8AuthReply");
		ctx.get().enqueueWork(() -> message.handle(ctx.get()));
		ctx.get().setPacketHandled(true);
	}
}