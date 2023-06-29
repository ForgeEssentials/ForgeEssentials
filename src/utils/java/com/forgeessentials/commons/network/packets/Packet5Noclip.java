package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet5Noclip implements IFEPacket {
	public boolean noclip;

	public Packet5Noclip(boolean noclip) {
		this.noclip = noclip;
	}

	public static Packet5Noclip decode(PacketBuffer buf) {
		return new Packet5Noclip(buf.readBoolean());
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBoolean(noclip);
	}

	public boolean getNoclip() {
		return noclip;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		NetworkUtils.feletworklog.warn("Packet5Noclip was not handled properly");
	}

	public static void handler(final Packet5Noclip message, Supplier<NetworkEvent.Context> ctx) {
		NetworkUtils.feletworklog.info("Recieved Packet5Noclip");
		ctx.get().enqueueWork(() -> message.handle(ctx.get()));
		ctx.get().setPacketHandled(true);
	}
}
