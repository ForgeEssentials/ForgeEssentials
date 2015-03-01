package com.forgeessentials.core.preloader.asm;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.SignEditEvent;

public class FEHooks
{

    public static String[] onSignEditEvent(C12PacketUpdateSign data, EntityPlayerMP player)
    {
        SignEditEvent e = new SignEditEvent(data.func_149588_c(), data.func_149586_d(), data.func_149585_e(), data.func_149589_f(), player);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            return null;
        }
        return e.text;

    }
}
