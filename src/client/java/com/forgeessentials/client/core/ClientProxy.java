package com.forgeessentials.client.core;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.cui.CUIPlayerLogger;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.cui.CUIRollback;
import com.forgeessentials.client.network.C0PacketHandshake;
import com.forgeessentials.client.network.C1PacketSelectionUpdate;
import com.forgeessentials.client.util.DummyProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class ClientProxy extends DummyProxy
{
    private ClientConfig config;

    public void doPreInit(FMLPreInitializationEvent e)
    {

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            config = new ClientConfig(new Configuration(e.getSuggestedConfigurationFile()));
            config.init();
        }
    }
    public void load(FMLInitializationEvent e)
    {
        super.load(e);

        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        if (ForgeEssentialsClient.allowCUI)
        {
            MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
            MinecraftForge.EVENT_BUS.register(new CUIPlayerLogger());
            MinecraftForge.EVENT_BUS.register(new CUIRollback());
        }
        ForgeEssentialsClient.netHandler.registerMessage(C0PacketHandshake.class, C0PacketHandshake.Message.class, 0, Side.SERVER);
        ForgeEssentialsClient.netHandler.registerMessage(C1PacketSelectionUpdate.class, C1PacketSelectionUpdate.Message.class, 1, Side.CLIENT);
    }
}
