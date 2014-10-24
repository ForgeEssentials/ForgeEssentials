package com.forgeessentials.core.preloader.forge;

import com.forgeessentials.util.events.forge.SignEditEvent;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class network_NetHandlerPlayServer
{
    // patch method
    public static void processUpdateSign(NetHandlerPlayServer net, C12PacketUpdateSign p_147343_1_)
    {
        net.playerEntity.func_143004_u();
        WorldServer worldserver = net.serverController.worldServerForDimension(net.playerEntity.dimension);

        if (worldserver.blockExists(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e()))
        {
            TileEntity tileentity = worldserver.getTileEntity(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e());

            if (tileentity instanceof TileEntitySign)
            {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (!tileentitysign.func_145914_a() || tileentitysign.func_145911_b() != net.playerEntity)
                {
                    net.serverController.logWarning("Player " + net.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                    return;
                }
            }

            String[] text = onSignEditEvent(net, p_147343_1_);
            if (text == null)return;

            int i;
            int j;

            for (j = 0; j < 4; ++j)
            {
                boolean flag = true;

                if (text[j].length() > 15)
                {
                    flag = false;
                }
                else
                {
                    for (i = 0; i < text[j].length(); ++i)
                    {
                        if (!ChatAllowedCharacters.isAllowedCharacter(text[j].charAt(i)))
                        {
                            flag = false;
                        }
                    }
                }

                if (!flag)
                {
                    text[j] = "!?";
                }
            }

            if (tileentity instanceof TileEntitySign)
            {
                j = p_147343_1_.func_149588_c();
                int k = p_147343_1_.func_149586_d();
                i = p_147343_1_.func_149585_e();
                TileEntitySign tileentitysign1 = (TileEntitySign)tileentity;
                System.arraycopy(text, 0, tileentitysign1.signText, 0, 4);
                tileentitysign1.markDirty();
                worldserver.markBlockForUpdate(j, k, i);
            }
        }
    }

    // helper method
    public static String[] onSignEditEvent(NetHandlerPlayServer net, C12PacketUpdateSign data)
    {
        SignEditEvent e = new SignEditEvent(data.func_149588_c(), data.func_149586_d(), data.func_149585_e(), data.func_149589_f(), net.playerEntity);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            return null;
        }
        return e.text;

    }
}
