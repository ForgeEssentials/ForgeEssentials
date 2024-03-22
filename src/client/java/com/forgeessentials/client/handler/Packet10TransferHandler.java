package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet10ClientTransfer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class Packet10TransferHandler extends Packet10ClientTransfer
{
    public Packet10TransferHandler(String destinationAddress, String destinationAddressName, String fallbackAddress, String fallbackAddressName, boolean sendNow)
    {
        super(destinationAddress, destinationAddressName, fallbackAddress, fallbackAddressName, sendNow);
    }

    public static Packet10TransferHandler decode(FriendlyByteBuf buf)
    {
        return new Packet10TransferHandler(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readBoolean());
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        if(!fallbackAddress.equals("blank")) {
            ForgeEssentialsClient.fallback=fallbackAddress;
            ForgeEssentialsClient.fallbackName=fallbackAddressName;
        }
        if(!destinationAddress.equals("blank")) {
            ForgeEssentialsClient.redirect=destinationAddress;
            ForgeEssentialsClient.redirectName=destinationAddressName;
        }
        if(sendNow&&ForgeEssentialsClient.hasRedirect()) {
            ForgeEssentialsClient.transfer(ForgeEssentialsClient.redirect, ForgeEssentialsClient.redirectName);
        }
    }
}