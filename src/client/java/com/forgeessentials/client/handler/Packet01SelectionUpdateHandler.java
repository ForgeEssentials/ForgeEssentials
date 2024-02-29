package com.forgeessentials.client.handler;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet01SelectionUpdate;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet01SelectionUpdateHandler extends Packet01SelectionUpdate
{

    public Packet01SelectionUpdateHandler(Selection sel)
    {
        super(sel);
    }

    public static Packet01SelectionUpdateHandler decode(PacketBuffer byteBuf)
    {
    	Point point1 = null;
    	Point point2 = null;
    	if(byteBuf.readBoolean()) {
    		point1 = new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble());
    	}
    	if(byteBuf.readBoolean()) {
    		point2 = new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble());
    	}
    	if(point1!=null||point2!=null) {
    		Minecraft mc = Minecraft.getInstance();
    		Selection selection = new Selection(mc.player.level.dimension().getRegistryName().toString(), point1, point2);
    		return new Packet01SelectionUpdateHandler(selection);
    	}
        return new Packet01SelectionUpdateHandler(null);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        ForgeEssentialsClient.cuiRenderer.selection = selection;
    }
}
