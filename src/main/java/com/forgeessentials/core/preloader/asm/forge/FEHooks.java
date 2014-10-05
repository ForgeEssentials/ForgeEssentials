package com.forgeessentials.core.preloader.asm.forge;

import com.forgeessentials.util.events.forge.SignEditEvent;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraftforge.common.MinecraftForge;

public class FEHooks {

    public static String[] onSignEditEvent(NetHandlerPlayServer net, C12PacketUpdateSign data)
    {
        System.out.println("derp");

        SignEditEvent e = new SignEditEvent(data.func_149588_c(), data.func_149586_d(), data.func_149585_e(), data.func_149589_f(), net.playerEntity);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            return null;
        }
        return e.text;

    }
}
