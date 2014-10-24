package com.forgeessentials.core.preloader.forge;

import com.forgeessentials.core.preloader.asm.EventInjector.MethodMapping;
import com.forgeessentials.util.events.forge.SignEditEvent;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

public class network_NetHandlerPlayServer extends MethodMapping
{
    public network_NetHandlerPlayServer()
    {
        super("func_147343_a", "processUpdateSign", "(Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V");
    }

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

    // actual method definition
    @Override
    public void defineMethod(ClassWriter classWriter) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, getName(), "(Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/forgeessentials/core/preloader/forge/network_NetHandlerPlayServer", mcpName, "(Lnet/minecraft/network/NetHandlerPlayServer;Lnet/minecraft/network/play/client/C12PacketUpdateSign;)V", false);
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lnet/minecraft/network/NetHandlerPlayServer;", null, l0, l1, 0);
        mv.visitLocalVariable("packet", "Lnet/minecraft/network/play/client/C12PacketUpdateSign;", null, l0, l1, 1);
        mv.visitMaxs(2, 2); // change this
        mv.visitEnd();
    }
}
