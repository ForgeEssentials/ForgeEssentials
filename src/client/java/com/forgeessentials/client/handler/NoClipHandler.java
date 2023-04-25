package com.forgeessentials.client.handler;

import com.forgeessentials.commons.network.packets.Packet5Noclip;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class NoClipHandler extends Packet5Noclip
{
	@Override
	public void handle(Context context) {
		Packet5Noclip packet5Noclip = new Packet5Noclip();
		Minecraft instance = Minecraft.getInstance();
		instance.player.noPhysics = packet5Noclip.noclip;
		context.setPacketHandled(true);
	}
}
