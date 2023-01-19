package com.forgeessentialsclient;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.FMLHandshakeMessages.S2CModList;
import net.minecraftforge.fml.network.NetworkEvent;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentialsclient.commands.CommandInit;
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
import com.forgeessentialsclient.utils.commons.network.packets.Packet3PlayerPermissions;
import com.forgeessentialsclient.utils.commons.network.packets.Packet5Noclip;
import com.forgeessentialsclient.utils.commons.network.packets.Packet6AuthLogin;
import com.forgeessentialsclient.utils.commons.network.packets.Packet7Remote;

@Mod(ForgeEssentialsClient.MODID)
@Mod.EventBusSubscriber(modid = ForgeEssentialsClient.MODID, bus = Bus.MOD,value = Dist.CLIENT)
public class ForgeEssentialsClient
{
    
    public static final String MODID = "forgeessentialsclient";
    public static final String MODNAME = "ForgeEssentialClientAddon";
    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    //@SidedProxy(clientSide = "com.forgeessentialsclientclient.core.ClientProxy", serverSide = "com.forgeessentialsclientclient.core.CommonProxy")

    public static ModContainer MOD_CONTAINER;
    
    protected static boolean serverHasFE;
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
    
    public ForgeEssentialsClient(){
    	//Set mod as client sdie only
    	MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
    	MOD_CONTAINER.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()->Pair.of(
  		      ()->"anything. i don't care", // if i'm actually on the server, this string is sent but i'm a client only mod, so it won't be
  		      (remoteversionstring,networkbool)->networkbool));// i accept anything from the server, by returning true if it's asking about the server
    	//Get EventBus
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	//Register Listeners
    	bus.addListener(this::commonsetup);
    	bus.addListener(this::onConfigLoad);
    	
    	//Register our config files
    	registerConfig();
    	MinecraftForge.EVENT_BUS.register(this);
    }
    /* ------------------------------------------------------------ */
    
    @SubscribeEvent
    public void getServerMods(NetworkEvent.LoginPayloadEvent e)
    {
	PacketBuffer payload= e.getPayload();
	S2CModList list = FMLHandshakeMessages.S2CModList.decode(payload);
        if (list.getModList().contains("forgeessentials"))
        {
        	serverHasFE = true;
            feclientlog.info("The server is running ForgeEssentials.");
        }
    }

    public void commonsetup(FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist.isClient()) {
        	registerNetworkMessages();
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

        } else {
            System.err.println("ForgeEssentials client does nothing on servers. You should remove it!");
        }
    }
    
    @SubscribeEvent
	public void onCommandRegister(final RegisterCommandsEvent event) {
    	CommandInit.registerCommands(event);
	}
    
    private static void registerNetworkMessages()
    {
        // Register network messages
        NetworkUtils.registerClientToServer(0, Packet0Handshake.class, Packet0Handshake::decode);
        NetworkUtils.registerServerToClient(1, Packet1SelectionUpdate.class, Packet1SelectionUpdate::decode);
		//NetworkUtils.registerServerToClient(2, Packet2Reach.class, Packet2Reach::decode);
        NetworkUtils.registerServerToClient(3, Packet3PlayerPermissions.class, Packet3PlayerPermissions::decode);
		//NetworkUtils.registerServerToClient(2, Packet4Economy.class, Packet4Economy::decode); //heck why not add something to space 4
        NetworkUtils.registerServerToClient(5, Packet5Noclip.class, Packet5Noclip::decode);
        NetworkUtils.registerServerToClient(6, Packet6AuthLogin.class, Packet6AuthLogin::decode);
        NetworkUtils.registerServerToClient(7, Packet7Remote.class, Packet7Remote::decode);
    }
    private void onConfigLoad(ModConfigEvent configEvent) {
		//Note: We listen to both the initial load and the reload, so as to make sure that we fix any accidentally
		// cached values from calls before the initial loading
		ModConfig config = configEvent.getConfig();
		//Make sure it is for the same modid as us
		if (config.getModId().equals(MODID) && config instanceof FEModConfig) {
			FEModConfig feConfig = (FEModConfig) configEvent.getConfig();
			feConfig.clearListenerCache();
		}
	}

    /* ------------------------------------------------------------ */

    public static boolean serverHasFE()
    {
        return serverHasFE;
    }
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
    /* ------------------------------------------------------------ */
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
