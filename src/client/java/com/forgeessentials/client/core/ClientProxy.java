package com.forgeessentials.client.core;

import static com.forgeessentials.client.ForgeEssentialsClient.feclientlog;
import static com.forgeessentials.commons.network.NetworkUtils.netHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.cui.CUIRenderrer;
import com.forgeessentials.client.network.C5HandlerNoclip;
import com.forgeessentials.client.network.C6HandlerSpeed;
import com.forgeessentials.client.network.C7HandlerRemote;
import com.forgeessentials.client.remote.QRRenderer;
import com.forgeessentials.client.util.DummyProxy;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.commons.network.Packet6Speed;
import com.forgeessentials.commons.network.Packet7Remote;
import com.forgeessentials.commons.selections.Selection;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends DummyProxy
{
    private ClientConfig config;

    protected boolean sentHandshake = true;

    private static Selection selection;

    private static ResourceLocation qrCode;

    public static ClientProxy INSTANCE;

    public ClientProxy()
    {
        INSTANCE = this;
    }

    @Override
    public void doPreInit(FMLPreInitializationEvent e)
    {
        BuildInfo.getBuildInfo(e.getSourceFile());
        feclientlog.info(String.format("Running ForgeEssentials client %s #%d (%s)", //
                BuildInfo.VERSION, BuildInfo.getBuildNumber(), BuildInfo.getBuildHash()));

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            config = new ClientConfig(new Configuration(e.getSuggestedConfigurationFile()));
            config.init();
        }
        netHandler.registerMessage(new IMessageHandler<Packet1SelectionUpdate, IMessage>() {
            @Override
            public IMessage onMessage(Packet1SelectionUpdate message, MessageContext ctx)
            {
                setSelection(message.getSelection());
                return null;
            }
        }, Packet1SelectionUpdate.class, 1, Side.CLIENT);
        netHandler.registerMessage(C5HandlerNoclip.class, Packet5Noclip.class, 5, Side.CLIENT);
        netHandler.registerMessage(C6HandlerSpeed.class, Packet6Speed.class, 6, Side.CLIENT);
        netHandler.registerMessage(C7HandlerRemote.class, Packet7Remote.class, 7, Side.CLIENT);

        if (!Loader.isModLoaded("ForgeEssentials"))
        {
            NetworkUtils.initClientNullHandlers();
        }
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
        ClientCommandHandler.instance.registerCommand(new FEClientCommand());
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
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        sentHandshake = false;
    }

    @SubscribeEvent
    public void clientTickEvent(TickEvent.ClientTickEvent event)
    {
        if (!sentHandshake && ForgeEssentialsClient.instance.serverHasFE)
        {
            sentHandshake = true;
            NetworkUtils.netHandler.sendToServer(new Packet0Handshake());
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
