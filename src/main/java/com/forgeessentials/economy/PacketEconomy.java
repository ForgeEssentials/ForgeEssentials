package com.forgeessentials.economy;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.WorldServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.network.IForgeEssentialsPacket;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketEconomy implements IForgeEssentialsPacket {

	private Packet250CustomPayload packet;
	
	public PacketEconomy(int amount){
		packet = new Packet250CustomPayload();

		ByteArrayOutputStream streambyte = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(streambyte);

		try
		{
			stream.write(3);

			stream.write(amount);
			

			stream.close();
			streambyte.close();

			packet.channel = FECHANNEL;
			packet.data = streambyte.toByteArray();
			packet.length = packet.data.length;
		}
		
		catch (Exception e)
		{
			OutputHandler.felog.info("Error creating packet >> " + this.getClass());
		}
	}

	@Override
	public Packet250CustomPayload getPayload() {
		// TODO Auto-generated method stub
		return packet;
	}
	
	public static void readServer(DataInputStream stream, WorldServer world,
			EntityPlayer player) {
		PacketEconomy packet = new PacketEconomy(APIRegistry.wallet.getWallet(player.username));
		PacketDispatcher.sendPacketToPlayer(packet.getPayload(), (Player) player);
	}

}
