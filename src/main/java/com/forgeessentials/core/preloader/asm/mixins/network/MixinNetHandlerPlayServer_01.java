package com.forgeessentials.core.preloader.asm.mixins.network;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_01
{
    
//    @Shadow
//    public MinecraftServer serverController;
//
//    @Shadow
//    public EntityPlayerMP playerEntity;
//
//    @Overwrite
//    public void processUpdateSign(C12PacketUpdateSign p_147343_1_)
//    {
//        playerEntity.func_143004_u();
//        WorldServer worldserver = serverController.worldServerForDimension(playerEntity.dimension);
//
//        if (worldserver.blockExists(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e()))
//        {
//            TileEntity tileentity = worldserver.getTileEntity(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e());
//
//            if (tileentity instanceof TileEntitySign)
//            {
//                TileEntitySign tileentitysign = (TileEntitySign) tileentity;
//
//                if (!tileentitysign.func_145914_a() || tileentitysign.func_145911_b() != playerEntity)
//                {
//                    serverController.logWarning("Player " + playerEntity.getName() + " just tried to change non-editable sign");
//                    return;
//                }
//            }
//
//            int i;
//            int j;
//
//            for (j = 0; j < 4; ++j)
//            {
//                boolean flag = true;
//
//                if (p_147343_1_.func_149589_f()[j].length() > 15)
//                {
//                    flag = false;
//                }
//                else
//                {
//                    for (i = 0; i < p_147343_1_.func_149589_f()[j].length(); ++i)
//                    {
//                        if (!ChatAllowedCharacters.isAllowedCharacter(p_147343_1_.func_149589_f()[j].charAt(i)))
//                        {
//                            flag = false;
//                        }
//                    }
//                }
//
//                if (!flag)
//                {
//                    p_147343_1_.func_149589_f()[j] = "!?";
//                }
//            }
//
//            if (tileentity instanceof TileEntitySign)
//            {
//                j = p_147343_1_.func_149588_c();
//                int k = p_147343_1_.func_149586_d();
//                i = p_147343_1_.func_149585_e();
//                TileEntitySign tileentitysign1 = (TileEntitySign) tileentity;
//                System.arraycopy(Preconditions.checkNotNull(onSignEditEvent(p_147343_1_, playerEntity)), 0, tileentitysign1.signText, 0, 4);
//                tileentitysign1.markDirty();
//                worldserver.markBlockForUpdate(j, k, i);
//            }
//        }
//    }
//
//    // helper method
//    private String[] onSignEditEvent(C12PacketUpdateSign data, EntityPlayerMP player)
//    {
//        SignEditEvent e = new SignEditEvent(data.func_149588_c(), data.func_149586_d(), data.func_149585_e(), data.func_149589_f(), player);
//        if (MinecraftForge.EVENT_BUS.post(e))
//        {
//            return null;
//        }
//        return e.text;
//
//    }

}
