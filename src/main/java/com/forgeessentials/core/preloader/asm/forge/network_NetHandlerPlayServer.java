package com.forgeessentials.core.preloader.asm.forge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldServer;

public class network_NetHandlerPlayServer extends NetHandlerPlayServer{

    private final MinecraftServer serverController;

    private network_NetHandlerPlayServer(MinecraftServer p_i1530_1_, NetworkManager p_i1530_2_, EntityPlayerMP p_i1530_3_)
    {
        super(p_i1530_1_, p_i1530_2_, p_i1530_3_);
        this.serverController = p_i1530_1_;
    }

    @Override
    public void processUpdateSign(C12PacketUpdateSign p_147343_1_)
    {
        this.playerEntity.func_143004_u();
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);

        if (worldserver.blockExists(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e()))
        {
            TileEntity tileentity = worldserver.getTileEntity(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e());

            if (tileentity instanceof TileEntitySign)
            {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (!tileentitysign.func_145914_a() || tileentitysign.func_145911_b() != this.playerEntity)
                {
                    this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                    return;
                }
            }

            String[] text = FEHooks.onSignEditEvent(this, p_147343_1_);
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
}
