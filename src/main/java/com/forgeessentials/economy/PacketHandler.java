package com.forgeessentials.economy;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player playerFake) {
		try {
			ByteArrayInputStream streambyte = new ByteArrayInputStream(packet.data);
			DataInputStream stream = new DataInputStream(streambyte);

			EntityPlayer player = (EntityPlayer) playerFake;
			WorldServer world = (WorldServer) player.worldObj;

			int ID = stream.read();

			switch (ID) {
			// cast to the correct instance of ForgeEssentialsPacketbase and use
			// the read methods.
			case 4:
				PacketEconomy.readServer(stream, world, player);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}