package com.forgeessentialsclient.core;

import static com.forgeessentialsclient.ForgeEssentialsClient.feclientlog;


import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import com.forgeessentialsclient.ForgeEssentialsClient;
import com.forgeessentialsclient.config.ClientConfig;
import com.forgeessentialsclient.config.FEModConfig;
import com.forgeessentialsclient.config.IFEConfig;
import com.forgeessentialsclient.handler.CUIRenderrer;
import com.forgeessentialsclient.handler.PermissionOverlay;
import com.forgeessentialsclient.handler.QRRenderer;
import com.forgeessentialsclient.handler.QuestionerKeyHandler;
import com.forgeessentialsclient.utils.commons.BuildInfo;
import com.forgeessentialsclient.utils.commons.network.NetworkUtils;
import com.forgeessentialsclient.utils.commons.network.packets.Packet0Handshake;
import com.forgeessentialsclient.utils.commons.network.packets.Packet1SelectionUpdate;
import com.forgeessentialsclient.utils.commons.network.packets.Packet2Reach;
import com.forgeessentialsclient.utils.commons.network.packets.Packet3PlayerPermissions;
import com.forgeessentialsclient.utils.commons.network.packets.Packet5Noclip;
import com.forgeessentialsclient.utils.commons.network.packets.Packet6AuthLogin;
import com.forgeessentialsclient.utils.commons.network.packets.Packet7Remote;

public class ClientProxy
{

    /* ------------------------------------------------------------ */

    public static final ClientConfig client = new ClientConfig();

    private static int clientTimeTicked;

    private static boolean sentHandshake = true;

    /* ------------------------------------------------------------ */

    public static Boolean allowCUI, 
    allowQRCodeRender, 
    allowPermissionRender, 
    allowQuestionerShortcuts, 
    allowAuthAutoLogin;

    public static float reachDistance;

    /* ------------------------------------------------------------ */

    private static CUIRenderrer cuiRenderer = new CUIRenderrer();

    private static QRRenderer qrCodeRenderer = new QRRenderer();

    private static PermissionOverlay permissionOverlay = new PermissionOverlay();

    /* ------------------------------------------------------------ */

    public ClientProxy()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void doSetup(FMLCommonSetupEvent event)
    {
        BuildInfo.getBuildInfo(null/*event.getSourceFile()*/);
        feclientlog.info(String.format("Running ForgeEssentials client %s (%s)", BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));

        // Initialize with configuration options
        ClientConfig c = new ClientConfig();
        allowCUI = c.allowCUI.get();
        allowQRCodeRender = c. allowQRCodeRender.get();
        allowPermissionRender = c.allowPermissionRender.get();
        allowQuestionerShortcuts = c.allowQuestionerShortcuts.get();
        allowAuthAutoLogin = c.allowAuthAutoLogin.get();
        if (!c.versioncheck.get())
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

        registerNetworkMessages();
    }

    private static void registerNetworkMessages()
    {
        // Register network messages
        NetworkUtils.registerClientToServer(0, Packet0Handshake.class, Packet0Handshake::decode);
        NetworkUtils.registerServerToClient(1, Packet1SelectionUpdate.class, Packet1SelectionUpdate::decode);
		NetworkUtils.registerServerToClient(2, Packet2Reach.class, Packet2Reach::decode);
        NetworkUtils.registerServerToClient(3, Packet3PlayerPermissions.class, Packet3PlayerPermissions::decode);
        NetworkUtils.registerServerToClient(5, Packet5Noclip.class, Packet5Noclip::decode);
        NetworkUtils.registerServerToClient(6, Packet6AuthLogin.class, Packet6AuthLogin::decode);
        NetworkUtils.registerServerToClient(7, Packet7Remote.class, Packet7Remote::decode);
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


	public static void registerConfig() {
		registerConfig(client);
		
	}
	public static void registerConfig(IFEConfig config) {
		FEModConfig peModConfig = new FEModConfig(ForgeEssentialsClient.MOD_CONTAINER, config);
		if (config.addToContainer()) {
			ForgeEssentialsClient.MOD_CONTAINER.addConfig(peModConfig);
		}
	}

}
