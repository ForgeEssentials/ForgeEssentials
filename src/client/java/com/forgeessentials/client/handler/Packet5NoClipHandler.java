package com.forgeessentials.client.handler;

import com.forgeessentials.commons.network.packets.Packet5Noclip;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet5NoClipHandler extends Packet5Noclip
{
	public Packet5NoClipHandler(boolean noclip)
    {
        super(noclip);
    }

    @Override
	public void handle(Context context) {
		Minecraft instance = Minecraft.getInstance();
		instance.player.noPhysics = noclip;
	}
}
