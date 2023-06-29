package com.forgeessentials.commons.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IFEPacket {

	void handle(NetworkEvent.Context context);

	void encode(PacketBuffer buffer);

	// public <T> void handler(final Class<T> message,
	// Supplier<NetworkEvent.Context> ctx);
	// static <T> void handler(Class<T> input, Supplier<NetworkEvent.Context> ctx)
	// {}
}