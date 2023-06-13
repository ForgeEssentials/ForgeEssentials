package com.forgeessentials.client.handler;

import com.forgeessentials.commons.network.packets.Packet5Noclip;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet5NoClipHandler extends Packet5Noclip
{
    public Packet5NoClipHandler(boolean noclip)
    {
        super(noclip);
    }

    public static Packet5NoClipHandler decode(PacketBuffer buf)
    {
        return new Packet5NoClipHandler(buf.readBoolean());
    }

    @Override
	public void handle(NetworkEvent.Context context) {
		Minecraft instance = Minecraft.getInstance();
		instance.player.noPhysics = noclip;
	}
}
