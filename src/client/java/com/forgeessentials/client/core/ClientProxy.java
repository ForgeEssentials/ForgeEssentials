package com.forgeessentials.client.core;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.cui.CUIPlayerLogger;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.cui.CUIRollback;
import com.forgeessentials.client.network.C0PacketHandshake;
import com.forgeessentials.client.network.C1PacketSelectionUpdate;
import com.forgeessentials.client.network.C2PacketPlayerLogger;
import com.forgeessentials.client.network.C3PacketRollback;
import com.forgeessentials.client.network.C4PacketEconomy;
import com.forgeessentials.client.network.C5PacketNoclip;
import com.forgeessentials.client.util.DummyProxy;
import com.forgeessentials.commons.VersionUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import static com.forgeessentials.client.ForgeEssentialsClient.feclientlog;
import static com.forgeessentials.client.ForgeEssentialsClient.netHandler;

public class ClientProxy extends DummyProxy
{
    private ClientConfig config;

    @Override
    public void doPreInit(FMLPreInitializationEvent e)
    {
        feclientlog.info("Build information: Build number is: " + VersionUtils.getBuildNumber(e.getSourceFile()) + ", build hash is: " + VersionUtils.getBuildHash(e.getSourceFile()));
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            config = new ClientConfig(new Configuration(e.getSuggestedConfigurationFile()));
            config.init();
        }
        netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");
        netHandler.registerMessage(C0PacketHandshake.class, C0PacketHandshake.class, 0, Side.SERVER);
        netHandler.registerMessage(C1PacketSelectionUpdate.class, C1PacketSelectionUpdate.class, 1, Side.CLIENT);
        netHandler.registerMessage(C2PacketPlayerLogger.class, C2PacketPlayerLogger.class, 2, Side.CLIENT);
        netHandler.registerMessage(C3PacketRollback.class, C3PacketRollback.class, 3, Side.CLIENT);
        netHandler.registerMessage(C4PacketEconomy.class, C4PacketEconomy.class, 4, Side.CLIENT);
        netHandler.registerMessage(C5PacketNoclip.class, C5PacketNoclip.class, 5, Side.CLIENT);
    }
    
    @Override
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
    }
}
