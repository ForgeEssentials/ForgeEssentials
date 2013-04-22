package com.ForgeEssentials.chat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.packetInspector.IPacketAnalyzer;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public class PacketAnalyzerChat implements IPacketAnalyzer
{
    /*
     * We arn't expecting any incoming packets.
     */
    @Override
    public Packet analyzeIncoming(Packet packet)
    {
        return packet;
    }

    @Override
    public Packet analyzeOutgoing(Packet packet)
    {   
        if (packet instanceof Packet201PlayerInfo)
        {
            Packet201PlayerInfo p201 = (Packet201PlayerInfo) packet;
            EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(p201.playerName);
            if (player != null && player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).hasKey("nickname"))
            {
                p201.playerName = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getString("nickname");
            }
        }
        
        return packet;
    }
}