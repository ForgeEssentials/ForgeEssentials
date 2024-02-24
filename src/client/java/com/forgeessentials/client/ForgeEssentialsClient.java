package com.forgeessentials.client;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.auth.AuthAutoLogin;
import com.forgeessentials.client.commands.CommandInit;
import com.forgeessentials.client.config.ClientConfig;
import com.forgeessentials.client.config.FEModConfig;
import com.forgeessentials.client.config.IFEConfig;
import com.forgeessentials.client.handler.Packet01SelectionUpdateCUIRenderrer;
import com.forgeessentials.client.handler.Packet01SelectionUpdateHandler;
import com.forgeessentials.client.handler.Packet03PlayerPermissionsHandler;
import com.forgeessentials.client.handler.Packet03PlayerPermissionsOverlay;
import com.forgeessentials.client.handler.Packet05NoClipHandler;
import com.forgeessentials.client.handler.Packet06AuthLoginHandler;
import com.forgeessentials.client.handler.Packet07RemoteHandler;
import com.forgeessentials.client.handler.Packet07RemoteQRRenderer;
import com.forgeessentials.client.handler.Packet09AuthRequestHandler;
import com.forgeessentials.client.handler.Packet10TransferHandler;
import com.forgeessentials.client.handler.QuestionerKeyHandler;
import com.forgeessentials.client.mixin.FEClientMixinConfig;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet00Handshake;
import com.forgeessentials.commons.network.packets.Packet08AuthReply;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

@Mod(ForgeEssentialsClient.MODID)
@Mod.EventBusSubscriber(modid = ForgeEssentialsClient.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ForgeEssentialsClient
{

    public static final String MODID = "forgeessentialsclient";
    public static final String MODNAME = "ForgeEssentialClientAddon";
    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    // @SidedProxy(clientSide = "com.forgeessentialsclientclient.core.ClientProxy",
    // serverSide = "com.forgeessentialsclientclient.core.CommonProxy")

    public static ModContainer MOD_CONTAINER;

    private static File jarLocation;

    protected static boolean serverHasFE;
    /* ------------------------------------------------------------ */

    public static final ClientConfig client = new ClientConfig();

    private static int clientTimeTicked;

    private static boolean sentHandshake = true;

    /* ------------------------------------------------------------ */

    public static Boolean allowCUI, allowQRCodeRender, allowPermissionRender, allowQuestionerShortcuts,
            allowAuthAutoLogin;

    public static float reachDistance;

    public static Boolean noClip = false;
    public static Boolean noClipChanged = false;

    /* ------------------------------------------------------------ */

    public static String redirect = "";
    public static String redirectName = "";
    public static String fallback = "";
    public static String fallbackName = "";
    
    public static boolean joinedServer = false;

    public static boolean hasRedirect() {
        return !redirect.equals("");
    }
    public static boolean hasFallback() {
        return !fallback.equals("");
    }
    /* ------------------------------------------------------------ */

    public static AuthAutoLogin authDatabase = new AuthAutoLogin();

    public static Packet01SelectionUpdateCUIRenderrer cuiRenderer = new Packet01SelectionUpdateCUIRenderrer();

    public static Packet03PlayerPermissionsOverlay permissionOverlay = new Packet03PlayerPermissionsOverlay();

    public static Packet07RemoteQRRenderer qrCodeRenderer = new Packet07RemoteQRRenderer();

    /* ------------------------------------------------------------ */

    public ForgeEssentialsClient()
    {
        // Set mod as client side only
        MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
        MOD_CONTAINER.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "anything. i don't care",
                (remoteversionstring, networkbool) -> networkbool));
        // Get EventBus
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register Listeners
        bus.addListener(this::commonsetup);
        bus.addListener(this::onConfigLoad);

        // Register our config files
        registerConfig();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(cuiRenderer);
        MinecraftForge.EVENT_BUS.register(qrCodeRenderer);
        MinecraftForge.EVENT_BUS.register(permissionOverlay);
    }
    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void login(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof ClientPlayerEntity)
        {
        	BuildInfo.startVersionChecks(MODID);
            if (BuildInfo.isOutdated())
            {
                event.getEntity().sendMessage(new StringTextComponent("\u00A72[ForgeEssentials client]:\u00A7r A new version (\u00A73" + BuildInfo.getLatestVersion() + "\u00A7r) was found"), event.getEntity().getUUID());
            }
        }
    }

    public static void getServerMods(List<String> e)
    {
        if (e.contains("forgeessentials"))
        {
            serverHasFE = true;
            feclientlog.info("The server is running ForgeEssentials.");
        }
    }

    public void commonsetup(FMLCommonSetupEvent event)
    {
        if (FMLEnvironment.dist.isClient())
        {
            registerNetworkMessages();

            List<ModInfo> mods = ModList.get().getMods();
            for (ModInfo mod : mods)
            {
                if (mod.getModId().equals("forgeessentialsclient"))
                {
                    jarLocation = mod.getOwningFile().getFile().getFilePath().toFile();
                    break;
                }
            }
            BuildInfo.getBuildInfo(jarLocation);
            feclientlog.info(String.format("Running ForgeEssentials client %s (%s)-%s", BuildInfo.getCurrentVersion(),
                    BuildInfo.getBuildHash(), BuildInfo.getBuildType()));

            // Initialize with configuration options
            ClientConfig c = new ClientConfig();
            allowCUI = c.allowCUI.get();
            allowQRCodeRender = c.allowQRCodeRender.get();
            allowPermissionRender = c.allowPermissionRender.get();
            allowQuestionerShortcuts = c.allowQuestionerShortcuts.get();
            allowAuthAutoLogin = c.allowAuthAutoLogin.get();
            BuildInfo.needCheckVersion = c.versioncheck.get();

            if (allowCUI)
                MinecraftForge.EVENT_BUS.register(cuiRenderer);
            if (allowQRCodeRender)
                MinecraftForge.EVENT_BUS.register(qrCodeRenderer);
            if (allowPermissionRender)
                MinecraftForge.EVENT_BUS.register(permissionOverlay);
            if (allowQuestionerShortcuts)
                new QuestionerKeyHandler();
        }
        else
        {
            System.err.println("ForgeEssentials client does nothing on servers. You should remove it!");
        }
    }

    @SubscribeEvent
    public void onCommandRegister(final RegisterCommandsEvent event)
    {
        CommandInit.registerCommands(event);
    }

    private static void registerNetworkMessages()
    {
        // Register network messages
        NetworkUtils.registerClientToServer(0, Packet00Handshake.class, Packet00Handshake::encode, Packet00Handshake::decode, Packet00Handshake::handler);
        NetworkUtils.registerServerToClient(1, Packet01SelectionUpdateHandler.class, Packet01SelectionUpdateHandler::encode,
                Packet01SelectionUpdateHandler::decode, Packet01SelectionUpdateHandler::handler);
        // NetworkUtils.registerServerToClient(2, Packet2Reach.class, Packet2Reach::decode);
        NetworkUtils.registerServerToClient(3, Packet03PlayerPermissionsHandler.class, Packet03PlayerPermissionsHandler::encode,
                Packet03PlayerPermissionsHandler::decode, Packet03PlayerPermissionsHandler::handler);
        // NetworkUtils.registerServerToClient(4, Packet4Economy.class, Packet4Economy::decode); //heck why not add something to space 4
        NetworkUtils.registerServerToClient(5, Packet05NoClipHandler.class, Packet05NoClipHandler::encode, Packet05NoClipHandler::decode,
                Packet05NoClipHandler::handler);
        NetworkUtils.registerServerToClient(6, Packet06AuthLoginHandler.class, Packet06AuthLoginHandler::encode, Packet06AuthLoginHandler::decode,
                Packet06AuthLoginHandler::handler);
        NetworkUtils.registerServerToClient(7, Packet07RemoteHandler.class, Packet07RemoteHandler::encode, Packet07RemoteHandler::decode,
                Packet07RemoteHandler::handler);
        NetworkUtils.registerClientToServer(8, Packet08AuthReply.class, Packet08AuthReply::encode, Packet08AuthReply::decode, Packet08AuthReply::handler);
        NetworkUtils.registerServerToClient(9, Packet09AuthRequestHandler.class, Packet09AuthRequestHandler::encode, Packet09AuthRequestHandler::decode,
                Packet09AuthRequestHandler::handler);
        NetworkUtils.registerServerToClient(10, Packet10TransferHandler.class, Packet10TransferHandler::encode, Packet10TransferHandler::decode,
                Packet10TransferHandler::handler);

    }

    private void onConfigLoad(ModConfigEvent configEvent)
    {
        // Note: We listen to both the initial load and the reload, so as to make sure
        // that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        // Make sure it is for the same modid as us
        if (config.getModId().equals(MODID) && config instanceof FEModConfig)
        {
            FEModConfig feConfig = (FEModConfig) configEvent.getConfig();
            feConfig.clearListenerCache();
        }
    }

    /**
     * A temporary fix since forge does not have client commands in 1.16.5
     */
    @SubscribeEvent
    public void fecommandevent(ClientChatEvent event)
    {
        if (event.getOriginalMessage().equals("feclient"))
        {
            Minecraft instance = Minecraft.getInstance();
            instance.gui.getChat().addMessage(new StringTextComponent("/feclient info: Get FE client info"));
            instance.gui.getChat().addMessage(new StringTextComponent("/feclient reinit: Redo server handshake"));
            instance.gui.getChat()
                    .addMessage(new StringTextComponent("/feclient reinit force: Force send server handshake"));
            event.setCanceled(true);
        }
        if (event.getOriginalMessage().equals("feclient reinit"))
        {
            Minecraft instance = Minecraft.getInstance();
            ForgeEssentialsClient.resendHandshake();
            instance.gui.getChat().addMessage(new StringTextComponent("Resent handshake packet to server."));
            event.setCanceled(true);
        }
        if (event.getOriginalMessage().equals("feclient info"))
        {
            Minecraft instance = Minecraft.getInstance();
            instance.gui.getChat()
                    .addMessage(new StringTextComponent(String.format("Running ForgeEssentials client %s (%s)-%s",
                            BuildInfo.getCurrentVersion(), BuildInfo.getBuildHash(), BuildInfo.getBuildType())));
            if (BuildInfo.isOutdated()) {
            	instance.gui.getChat().addMessage(new StringTextComponent(String.format("Outdated! Latest build is #%s", BuildInfo.getLatestVersion())));
            }
            instance.gui.getChat().addMessage(new StringTextComponent(
                    "\"Please refer to https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/Team-Information if you would like more information about the FE developers."));
            instance.gui.getChat().addMessage(new StringTextComponent("Injected patches:"));
            for (String patch : FEClientMixinConfig.getInjectedPatches())
                instance.gui.getChat().addMessage(new StringTextComponent("- " + patch));
            event.setCanceled(true);
        }
        if (event.getOriginalMessage().equals("feclient reinit force"))
        {
            Minecraft instance = Minecraft.getInstance();
            sentHandshake = true;
            NetworkUtils.sendToServer(new Packet00Handshake());
            instance.gui.getChat().addMessage(new StringTextComponent("Force Sent handshake packet to server."));
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        if(joinedServer && mc.level != null) {
            fallback = "";
            fallbackName = "";
            return;
        }
        if(!joinedServer && mc.level != null) {
            joinedServer = true;
            return;
        }
        if(joinedServer && mc.level == null) {
            joinedServer = false;
            return;
        }
        if(!joinedServer && mc.level == null) {
            if(hasFallback()) {
                if (mc.screen instanceof DisconnectedScreen) {
                    String fall = fallback;
                    String name = fallbackName;
                    fallback = "";
                    fallbackName = "";
                    transfer(fall, name);
                } else if (mc.screen instanceof MainMenuScreen || mc.screen instanceof MultiplayerScreen) {
                    fallback = "";
                    fallbackName = "";
                }
            }
        }
    }

    public static void transfer(String destinationAddress, String destinationName) {
        if (!Minecraft.getInstance().isSameThread()) {
            throw new IllegalStateException("Not in the main thread");
        }

        feclientlog.info("Connecting to server: " + destinationName+":"+destinationAddress);

        final Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.level.disconnect();
        }
        if (mc.isLocalServer()) {
            mc.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
        } else {
            mc.clearLevel();
        }
        mc.setScreen(new MultiplayerScreen(new MainMenuScreen()));
        mc.setScreen(new ConnectingScreen(mc.screen, mc, new ServerData(destinationName, destinationAddress, false)));
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
        cuiRenderer.selection=null;
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
            // Minecraft instance = Minecraft.getInstance();
            // instance.gui.getChat().addMessage(new StringTextComponent("Sending Handshake Packet to FE Server"));
            try {
                NetworkUtils.sendToServer(new Packet00Handshake());
            }catch(NullPointerException e) {
                ForgeEssentialsClient.feclientlog.warn("Failed to send initialization Packet");
            }
        }
        else
        {
            // Minecraft instance = Minecraft.getInstance();
            // instance.gui.getChat().addMessage(new StringTextComponent("Server Does not have FE, can't send initialization Packet"));
            ForgeEssentialsClient.feclientlog.warn("Server Does not have FE, can't send initialization Packet");
        }
    }

    public static void resendHandshake()
    {
        sentHandshake = false;
    }

    public static void sentHandshake()
    {
        sentHandshake = true;
    }

    /* ------------------------------------------------------------ */
    public static void registerConfig()
    {
        registerConfig(client);

    }

    public static void registerConfig(IFEConfig config)
    {
        FEModConfig peModConfig = new FEModConfig(ForgeEssentialsClient.MOD_CONTAINER, config);
        if (config.addToContainer())
        {
            ForgeEssentialsClient.MOD_CONTAINER.addConfig(peModConfig);
        }
    }
}
