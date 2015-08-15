package com.forgeessentials.client.core;

import static com.forgeessentials.client.ForgeEssentialsClient.feclientlog;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.handler.CUIRenderrer;
import com.forgeessentials.client.handler.PermissionOverlay;
import com.forgeessentials.client.handler.QRRenderer;
import com.forgeessentials.client.handler.ReachDistanceHandler;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.NetworkUtils.NullMessageHandler;
import com.forgeessentials.commons.network.Packet0Handshake;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.Packet2Reach;
import com.forgeessentials.commons.network.Packet3PlayerPermissions;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.commons.network.Packet7Remote;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy
{

    public static final String CONFIG_CAT = Configuration.CATEGORY_GENERAL;

    /* ------------------------------------------------------------ */

    private static Configuration config;

    private static int clientTimeTicked;

    private static boolean sentHandshake = true;

    /* ------------------------------------------------------------ */

    public static boolean allowCUI;

    public static boolean allowQRCodeRender;

    public static float reachDistance;

    /* ------------------------------------------------------------ */

    private static CUIRenderrer cuiRenderer = new CUIRenderrer();

    private static QRRenderer qrCodeRenderer = new QRRenderer();

    private static PermissionOverlay permissionOverlay = new PermissionOverlay();

    private ReachDistanceHandler reachDistanceHandler = new ReachDistanceHandler();

    /* ------------------------------------------------------------ */

    public ClientProxy()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void doPreInit(FMLPreInitializationEvent event)
    {
        BuildInfo.getBuildInfo(event.getSourceFile());
        feclientlog.info(String.format("Running ForgeEssentials client %s (%s)", BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));

        // Initialize configuration
        config = new Configuration(event.getSuggestedConfigurationFile());
        loadConfig();

        registerNetworkMessages();
    }

    @Override
    public void load(FMLInitializationEvent event)
    {
        super.load(event);
        ClientCommandHandler.instance.registerCommand(new FEClientCommand());
    }

    private void registerNetworkMessages()
    {
        // Register network messages
        NetworkUtils.registerMessageProxy(Packet0Handshake.class, 0, Side.SERVER, new NullMessageHandler<Packet0Handshake>() {
            /* dummy */
        });
        NetworkUtils.registerMessage(cuiRenderer, Packet1SelectionUpdate.class, 1, Side.CLIENT);
        NetworkUtils.registerMessage(reachDistanceHandler, Packet2Reach.class, 2, Side.CLIENT);
        NetworkUtils.registerMessage(permissionOverlay, Packet3PlayerPermissions.class, 3, Side.CLIENT);
        NetworkUtils.registerMessage(new IMessageHandler<Packet5Noclip, IMessage>() {
            @Override
            public IMessage onMessage(Packet5Noclip message, MessageContext ctx)
            {
                FMLClientHandler.instance().getClientPlayerEntity().noClip = message.getNoclip();
                return null;
            }
        }, Packet5Noclip.class, 5, Side.CLIENT);
        NetworkUtils.registerMessage(qrCodeRenderer, Packet7Remote.class, 7, Side.CLIENT);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equals(ForgeEssentialsClient.MODID))
            loadConfig();
    }

    private void loadConfig()
    {
        config.load();
        config.addCustomCategoryComment(CONFIG_CAT, "Configure ForgeEssentials Client addon features.");

        allowCUI = config.getBoolean("allowCUI", Configuration.CATEGORY_GENERAL, true, "Set to false to disable graphical selections.");
        allowQRCodeRender = config.get(Configuration.CATEGORY_GENERAL, "allowQRCodeRender", true,
                "Set to false to disable QR code rendering when you enter /remote qr..").getBoolean(true);

        if (allowCUI)
            MinecraftForge.EVENT_BUS.register(cuiRenderer);
        else
            MinecraftForge.EVENT_BUS.unregister(cuiRenderer);

        if (allowQRCodeRender)
            MinecraftForge.EVENT_BUS.register(qrCodeRenderer);
        else
            MinecraftForge.EVENT_BUS.unregister(qrCodeRenderer);

        config.save();
    }

    public static Configuration getConfig()
    {
        return config;
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void connectionOpened(FMLNetworkEvent.ClientConnectedToServerEvent e)
    {
        clientTimeTicked = 0;
        sentHandshake = false;
    }

    @SubscribeEvent
    public void clientTickEvent(TickEvent.ClientTickEvent event)
    {
        clientTimeTicked++;
        if (!sentHandshake && clientTimeTicked > 20)
        {
            sentHandshake = true;
            sendClientHandshake();
        }
    }

    public void sendClientHandshake()
    {
        if (ForgeEssentialsClient.serverHasFE())
            NetworkUtils.netHandler.sendToServer(new Packet0Handshake());
    }

    public static void resendHandshake()
    {
        sentHandshake = false;
    }

}
