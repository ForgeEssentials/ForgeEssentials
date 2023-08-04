package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet10ClientTransfer implements IFEPacket
{
    public String destinationAddress;
    public String destinationAddressName;
    public String fallbackAddress;
    public String fallbackAddressName;
    public boolean sendNow;

    public Packet10ClientTransfer(String destinationAddress, String destinationAddressName, String fallbackAddress, String fallbackAddressName, boolean sendNow){
        if(destinationAddress==null) {
            this.destinationAddress="blank";
        }else {this.destinationAddress = destinationAddress;}

        if(destinationAddressName==null) {
            this.destinationAddressName="blank";
        }else {this.destinationAddressName = destinationAddressName;}

        if(fallbackAddress==null) {
            this.fallbackAddress="blank";
        }else {this.fallbackAddress = fallbackAddress;}

        if(fallbackAddressName==null) {
            this.fallbackAddressName="blank";
        }else {this.fallbackAddressName = fallbackAddressName;}

        this.sendNow = sendNow;
    }

    public static Packet10ClientTransfer decode(PacketBuffer buf)
    {
        return new Packet10ClientTransfer(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readBoolean());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(destinationAddress);
        buf.writeUtf(destinationAddressName);
        buf.writeUtf(fallbackAddress);
        buf.writeUtf(fallbackAddressName);
        buf.writeBoolean(sendNow);
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet10ClientTransfer message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
