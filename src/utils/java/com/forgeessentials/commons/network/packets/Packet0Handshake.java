package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet0Handshake implements IFEPacket {

	public Packet0Handshake() {
	}

	public static Packet0Handshake decode(PacketBuffer buf) {
		return new Packet0Handshake();
	}

	@Override
	public void encode(PacketBuffer buf) {
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		NetworkUtils.feletworklog.warn("Packet0Handshake was not handled properly");
	}

	public static void handler(final Packet0Handshake message, Supplier<NetworkEvent.Context> ctx) {
		NetworkUtils.feletworklog.info("Recieved Packet0Handshake");
		ctx.get().enqueueWork(() -> message.handle(ctx.get()));
		ctx.get().setPacketHandled(true);
	}
}
