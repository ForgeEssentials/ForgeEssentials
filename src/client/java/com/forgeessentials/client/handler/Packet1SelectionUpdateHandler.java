package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet1SelectionUpdate;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet1SelectionUpdateHandler extends Packet1SelectionUpdate
{

    public Packet1SelectionUpdateHandler(Selection sel)
    {
        super(sel);
    }
    
    public static Packet1SelectionUpdateHandler decode(PacketBuffer byteBuf)
    {
        Selection selection = new Selection(
                byteBuf.readUtf(),
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null);
        return new Packet1SelectionUpdateHandler(selection);
    }
    
    @Override
    public void handle(NetworkEvent.Context context) {
        ForgeEssentialsClient.cuiRenderer.selection=selection;
    }
}
