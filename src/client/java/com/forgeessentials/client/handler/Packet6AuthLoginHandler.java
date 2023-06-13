package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.auth.AuthAutoLogin;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;


public class Packet6AuthLoginHandler extends Packet6AuthLogin
{
    public Packet6AuthLoginHandler(int mode, String hash) {
        super(mode, hash);
    }

    public static Packet6AuthLoginHandler decode(PacketBuffer buf) {
        return new Packet6AuthLoginHandler(buf.readInt(), buf.readUtf());
    }

	@Override
	public void handle(NetworkEvent.Context context) {
        // send empty response if the client has disabled this
        if (!ForgeEssentialsClient.allowAuthAutoLogin) {
        	NetworkUtils.sendToServer(new Packet6AuthLogin(1,""));
        }
        AuthAutoLogin.KEYSTORE = AuthAutoLogin.load();
        switch (mode)
        {
        case 0:
        	NetworkUtils.sendToServer(new Packet6AuthLogin(1, AuthAutoLogin.getKey(Minecraft.getInstance().getCurrentServer().ip)));
        case 2:
            AuthAutoLogin.setKey(Minecraft.getInstance().getCurrentServer().ip, hash);
            break;
        default:
            break;
        }
        context.setPacketHandled(true);
    }
}