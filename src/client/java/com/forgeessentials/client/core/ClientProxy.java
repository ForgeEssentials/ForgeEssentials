package com.forgeessentials.client.core;

import static com.forgeessentials.client.ForgeEssentialsClient.feclientlog;
import static com.forgeessentials.commons.NetworkUtils.netHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.network.C0PacketHandshake;
import com.forgeessentials.client.network.C1PacketSelectionUpdate;
import com.forgeessentials.client.network.C4PacketEconomy;
import com.forgeessentials.client.network.C5PacketNoclip;
import com.forgeessentials.client.network.C6PacketSpeed;
import com.forgeessentials.client.network.C7PacketRemote;
import com.forgeessentials.client.remote.QRRenderer;
import com.forgeessentials.client.util.DummyProxy;
import com.forgeessentials.commons.NetworkUtils;
import com.forgeessentials.commons.VersionUtils;
import com.forgeessentials.commons.selections.Selection;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends DummyProxy
{
    private ClientConfig config;
    
    private VersionUtils version;

    private static Selection selection;

    private static ResourceLocation qrCode;

    @Override
    public void doPreInit(FMLPreInitializationEvent e)
    {
        version = new VersionUtils(e.getSourceFile());
        feclientlog.info("Build information: Build number is: " + version.getBuildNumber() + ", build hash is: " + version.getBuildHash());
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            config = new ClientConfig(new Configuration(e.getSuggestedConfigurationFile()));
            config.init();
        }
        netHandler.registerMessage(C0PacketHandshake.class, C0PacketHandshake.class, 0, Side.SERVER);
        netHandler.registerMessage(C1PacketSelectionUpdate.class, C1PacketSelectionUpdate.class, 1, Side.CLIENT);
        netHandler.registerMessage(C4PacketEconomy.class, C4PacketEconomy.class, 4, Side.CLIENT);
        netHandler.registerMessage(C5PacketNoclip.class, C5PacketNoclip.class, 5, Side.CLIENT);
        netHandler.registerMessage(C6PacketSpeed.class, C6PacketSpeed.class, 6, Side.CLIENT);
        netHandler.registerMessage(C7PacketRemote.class, C7PacketRemote.class, 7, Side.CLIENT);
    }

    @Override
    public void load(FMLInitializationEvent e)
    {
        super.load(e);
        FMLCommonHandler.instance().bus().register(this);
        if (ForgeEssentialsClient.allowCUI)
        {
            MinecraftForge.EVENT_BUS.register(new CUIRenderrer());
        }
        MinecraftForge.EVENT_BUS.register(new QRRenderer());
    }

    @SubscribeEvent
    public void connectionOpened(FMLNetworkEvent.ClientConnectedToServerEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            selection = null;

    }

    @SubscribeEvent
    public void connectionClosed(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            selection = null;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent e)
    {
        if (ForgeEssentialsClient.instance.serverHasFE)
        {
            System.out.println("Dispatching FE handshake packet");
            NetworkUtils.netHandler.sendToServer(new C0PacketHandshake());
        }
    }

    public static Selection getSelection()
    {
        return selection;
    }

    public static void setSelection(Selection sel)
    {
        selection = sel;
    }

    public static ResourceLocation getQRCode()
    {
        return qrCode;
    }

    public static void setQRCode(ResourceLocation qrCode)
    {
        ClientProxy.qrCode = qrCode;
    }

}
