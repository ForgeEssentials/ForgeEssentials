package com.forgeessentials.core.preloader.mixin.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.SignEditEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_01 implements INetHandlerPlayServer, IUpdatePlayerListBox
{

    @Shadow
    public MinecraftServer serverController;

    @Shadow
    public EntityPlayerMP playerEntity;

    @Override
    @Overwrite
    public void processUpdateSign(C12PacketUpdateSign packetIn)
    {
        PacketThreadUtil.func_180031_a(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        WorldServer worldserver = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        BlockPos blockpos = packetIn.func_179722_a();

        if (worldserver.isBlockLoaded(blockpos))
        {
            TileEntity tileentity = worldserver.getTileEntity(blockpos);

            if (!(tileentity instanceof TileEntitySign))
            {
                return;
            }

            TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.getIsEditable() || tileentitysign.func_145911_b() != this.playerEntity)
            {
                this.serverController.logWarning("Player " + this.playerEntity.getName() + " just tried to change non-editable sign");
                return;
            }

            IChatComponent[] lines = onSignEditEvent(packetIn, playerEntity); if (lines != null){ return;}//FE: sign edit event

            for (int x = 0; x < tileentitysign.signText.length && x < lines.length; x++)
                tileentitysign.signText[x] = new ChatComponentText(net.minecraft.util.EnumChatFormatting.getTextWithoutFormattingCodes(lines[x]
                        .getUnformattedText()));

            tileentitysign.markDirty();
            worldserver.markBlockForUpdate(blockpos);
        }
    }

    private IChatComponent[] onSignEditEvent(C12PacketUpdateSign data, EntityPlayerMP player)
    {
        SignEditEvent e = new SignEditEvent(data.func_179722_a(), data.func_180768_b(), player);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            return null;
        }
        return e.text;

    }

}
