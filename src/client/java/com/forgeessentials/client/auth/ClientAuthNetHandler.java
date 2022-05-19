package com.forgeessentials.client.auth;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;

public class ClientAuthNetHandler extends Packet6AuthLogin
{
	@Override
	public void handle(Context context) {
		Packet6AuthLogin packet6AuthLogin= new Packet6AuthLogin();
        // send empty response if the client has disabled this
        if (!ClientProxy.allowAuthAutoLogin) {
        	NetworkUtils.sendToServer(new Packet6AuthLogin(1,""));
        }
        AuthAutoLogin.KEYSTORE = AuthAutoLogin.load();
        switch (packet6AuthLogin.mode)
        {
        case 0:
        	NetworkUtils.sendToServer(new Packet6AuthLogin(1, AuthAutoLogin.getKey(Minecraft.getInstance().getCurrentServer().ip)));
        case 2:
            AuthAutoLogin.setKey(Minecraft.getInstance().getCurrentServer().ip, packet6AuthLogin.hash);
            break;
        default:
            break;
        }
    }
}