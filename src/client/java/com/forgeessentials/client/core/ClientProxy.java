package com.forgeessentials.client.core;

import static com.forgeessentials.client.ForgeEssentialsClient.feclientlog;

import java.util.Optional;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.auth.ClientAuthNetHandler;
import com.forgeessentials.client.handler.CUIRenderrer;
import com.forgeessentials.client.handler.PermissionOverlay;
import com.forgeessentials.client.handler.QRRenderer;
import com.forgeessentials.client.handler.QuestionerKeyHandler;
import com.forgeessentials.client.handler.ReachDistanceHandler;
import com.forgeessentials.client.init.CommandInit;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.NetworkUtils.NullMessageHandler;
import com.forgeessentials.commons.network.packets.Packet0Handshake;
import com.forgeessentials.commons.network.packets.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.packets.Packet2Reach;
import com.forgeessentials.commons.network.packets.Packet3PlayerPermissions;
import com.forgeessentials.commons.network.packets.Packet5Noclip;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;
import com.forgeessentials.commons.network.packets.Packet7Remote;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class ClientProxy extends CommonProxy
{

    public static final String CONFIG_CAT = Configuration.CATEGORY_GENERAL;

    /* ------------------------------------------------------------ */

    private static Configuration config;

    private static int clientTimeTicked;

    private static boolean sentHandshake = true;

    /* ------------------------------------------------------------ */

    public static boolean allowCUI, allowQRCodeRender, allowPermissionRender, allowQuestionerShortcuts, allowAuthAutoLogin;

    public static float reachDistance;

    /* ------------------------------------------------------------ */

    private static CUIRenderrer cuiRenderer = new CUIRenderrer();

    private static QRRenderer qrCodeRenderer = new QRRenderer();

    private static PermissionOverlay permissionOverlay = new PermissionOverlay();

    private ReachDistanceHandler reachDistanceHandler = new ReachDistanceHandler();

    /* ------------------------------------------------------------ */

    public ClientProxy()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void doPreInit(FMLCommonSetupEvent event)
    {
        BuildInfo.getBuildInfo(event.getSourceFile());
        feclientlog.info(String.format("Running ForgeEssentials client %s (%s)", BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));

        // Initialize configuration
        config = new Configuration(event.getSuggestedConfigurationFile());
        loadConfig();

        registerNetworkMessages();
    }

    private static void registerNetworkMessages()
    {
    	NetworkUtils networkUtils = new NetworkUtils();
        // Register network messages
        networkUtils.registerClientToServer(0, Packet0Handshake.class, Packet0Handshake::encode);
        networkUtils.registerServerToClient(1, Packet1SelectionUpdate.class, Packet1SelectionUpdate::decode);
		networkUtils.registerServerToClient(2, Packet2Reach.class, Packet2Reach::decode);
        networkUtils.registerServerToClient(3, Packet3PlayerPermissions.class,Packet3PlayerPermissions::encode);
        networkUtils.registerServerToClient(new IMessageHandler<Packet5Noclip, IMessage>() {
            @Override
            public IMessage onMessage(Packet5Noclip message, MessageContext ctx)
            {
                FMLClientHandler.instance().getClientPlayerEntity().noClip = message.getNoclip();
                return null;
            }
        }, Packet5Noclip.class, 5, Side.CLIENT);
        networkUtils.registerServerToClient(new ClientAuthNetHandler(), Packet6AuthLogin.class, 6, Side.CLIENT);
        networkUtils.registerServerToClient(qrCodeRenderer, Packet7Remote.class, 7, Side.CLIENT);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(ForgeEssentialsClient.MODID))
            loadConfig();
    }

    private void loadConfig()
    {
        config.load();
        config.addCustomCategoryComment(CONFIG_CAT, "Configure ForgeEssentials Client addon features.");

        allowCUI = config.getBoolean("allowCUI", Configuration.CATEGORY_GENERAL, true, "Set to false to disable graphical selections.");
        allowQRCodeRender = config.get(Configuration.CATEGORY_GENERAL, "allowQRCodeRender", true,
                "Set to false to disable QR code rendering when you enter /remote qr.").getBoolean(true);
        allowPermissionRender = config.get(Configuration.CATEGORY_GENERAL, "allowPermRender", true,
                "Set to false to disable visual indication of block/item permissions").getBoolean(true);
        allowQuestionerShortcuts = config.get(Configuration.CATEGORY_GENERAL, "allowQuestionerShortcuts", true,
                "Use shortcut buttons to answer questions. Defaults are F8 for yes and F9 for no, change in game options menu.").getBoolean(true);
        allowAuthAutoLogin = config.get(Configuration.CATEGORY_GENERAL, "allowAuthAutoLogin", true,
                "Save tokens to automatically log in to servers using FE's Authentication Module.").getBoolean(true);
        if (!config.get(Configuration.CATEGORY_GENERAL, "versionCheck", true, "Check for newer versions of ForgeEssentials on load?").getBoolean())
            BuildInfo.checkVersion = false;

        if (allowCUI)
            MinecraftForge.EVENT_BUS.register(cuiRenderer);
        if (allowQRCodeRender)
            MinecraftForge.EVENT_BUS.register(qrCodeRenderer);
        if (allowPermissionRender)
            MinecraftForge.EVENT_BUS.register(permissionOverlay);
        if (allowQuestionerShortcuts)
            new QuestionerKeyHandler();
        BuildInfo.startVersionChecks();

        config.save();
    }

    public static Configuration getConfig()
    {
        return config;
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void connectionOpened(ClientPlayerNetworkEvent.LoggedInEvent e)
    {
        clientTimeTicked = 0;
        sentHandshake = false;
    }

    @SubscribeEvent
    public void clientTickEvent(ClientTickEvent.ClientTickEvent event)
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
        {
            ForgeEssentialsClient.feclientlog.info("Sending Handshake Packet to FE Server");
            NetworkUtils.sendToServer(new Packet0Handshake());
        }
        else
        {
            ForgeEssentialsClient.feclientlog.warn("Server Does not have FE, can't send initialization Packet");
        }
    }

    public static void resendHandshake()
    {
        sentHandshake = false;
    }

}
