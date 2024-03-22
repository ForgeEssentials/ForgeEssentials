package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet01SelectionUpdate;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class Packet01SelectionUpdateHandler extends Packet01SelectionUpdate
{

    public Packet01SelectionUpdateHandler(Selection sel)
    {
        super(sel);
    }

    public static Packet01SelectionUpdateHandler decode(FriendlyByteBuf byteBuf)
    {
        Selection selection = new Selection(byteBuf.readUtf(),
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble())
                        : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble())
                        : null);
        return new Packet01SelectionUpdateHandler(selection);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        ForgeEssentialsClient.cuiRenderer.selection = selection;
    }
}
