package com.forgeessentials.commons.network.packets;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import com.forgeessentials.commons.network.IFEPacket;


public class Packet6AuthLogin implements IFEPacket
{
    /*
    0 = request to get hash from client (hash will be empty!)
    1 = reply from client with hash (empty if client does not have hash)
    2 = request to put hash in client keystore
    3 = reply from client on keystore save (hash will be either SUCCESS or FAILURE)
     */
    public int mode;

    public String hash;

    // dummy ctor
    public Packet6AuthLogin() {}

    public Packet6AuthLogin(int mode, String hash)
    {
        this.mode = mode;
        this.hash = hash;
    }

    public static Packet6AuthLogin decode(PacketBuffer buf)
    {
    	return new Packet6AuthLogin(buf.readInt(),buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeInt(mode);
        buf.writeUtf(hash);
    }

	@Override
	public void handle(Context context) {}
}