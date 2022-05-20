package com.forgeessentials.client.core;

import static com.forgeessentials.client.ForgeEssentialsClient.feclientlog;

import java.nio.file.Path;
import java.util.Optional;

import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.auth.ClientAuthNetHandler;
import com.forgeessentials.client.config.BaseConfig;
import com.forgeessentials.client.config.ClientConfig;
import com.forgeessentials.client.config.FEModConfig;
import com.forgeessentials.client.config.IFEConfig;
import com.forgeessentials.client.config.ValuesCached.ValueCachedBoolean;
import com.forgeessentials.client.handler.CUIRenderrer;
import com.forgeessentials.client.handler.PermissionOverlay;
import com.forgeessentials.client.handler.QRRenderer;
import com.forgeessentials.client.handler.QuestionerKeyHandler;
import com.forgeessentials.client.handler.ReachDistanceHandler;
import com.forgeessentials.client.init.CommandInit;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet0Handshake;
import com.forgeessentials.commons.network.packets.Packet1SelectionUpdate;
import com.forgeessentials.commons.network.packets.Packet2Reach;
import com.forgeessentials.commons.network.packets.Packet3PlayerPermissions;
import com.forgeessentials.commons.network.packets.Packet5Noclip;
import com.forgeessentials.commons.network.packets.Packet6AuthLogin;
import com.forgeessentials.commons.network.packets.Packet7Remote;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class ClientProxy
{

    /* ------------------------------------------------------------ */

    public static final ClientConfig client = new ClientConfig();

    private static int clientTimeTicked;

    private static boolean sentHandshake = true;

    /* ------------------------------------------------------------ */

    public static Boolean allowCUI, allowQRCodeRender, allowPermissionRender, allowQuestionerShortcuts, allowAuthAutoLogin, versionCheck;

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

    public void doPreInit(FMLCommonSetupEvent event)
    {
        BuildInfo.getBuildInfo(event.getSourceFile());
        feclientlog.info(String.format("Running ForgeEssentials client %s (%s)", BuildInfo.getFullVersion(), BuildInfo.getBuildHash()));

        // Initialize with configuration options
        if (!versionCheck)
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
