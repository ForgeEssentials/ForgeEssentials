package com.forgeessentials.core.misc;

import com.forgeessentials.commons.network.packets.Packet0Handshake;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet0HandshakeHandler extends Packet0Handshake {
	public Packet0HandshakeHandler() {
	}

	public static Packet0HandshakeHandler decode(PacketBuffer buf) {
		return new Packet0HandshakeHandler();
	}

	@Override
	public void handle(Context context) {
		PlayerInfo.get(context.getSender()).setHasFEClient(true);
		LoggingHandler.felog.info(Translator.format("Recieved Handshake packet from %s",
				context.getSender().getDisplayName().getString()));
	}
}
