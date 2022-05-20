package com.forgeessentials.client;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.FMLHandshakeMessages.S2CModList;
import net.minecraftforge.fml.network.NetworkEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.config.FEModConfig;
import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.client.init.RegisterCommandEvent;

@Mod(ForgeEssentialsClient.MODID)
@Mod.EventBusSubscriber(modid = ForgeEssentialsClient.MODID, bus = Bus.MOD,value = Dist.CLIENT)
public class ForgeEssentialsClient
{
    
    public static final String MODID = "forgeessentialsclient";
    public static final String MODNAME = "ForgeEssentialClientAddon";
    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    //@SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.core.CommonProxy")
    protected static ClientProxy proxy;

    public static ModContainer MOD_CONTAINER;
    
    protected static boolean serverHasFE;
    
    public ForgeEssentialsClient(){
    	MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();
    	
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::commonsetup);
    	bus.addListener(this::onConfigLoad);
    	//Register our config files
    	ClientProxy.registerConfig();
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
        	
            //changedWindowTitle=null;
            //confHandler=ConfigurationHandler.getInstance();
            //confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
        	MinecraftForge.EVENT_BUS.register(RegisterCommandEvent.class);
        	proxy.doPreInit(event);
            //System.out.println("on Init, confHandler is "+confHandler);
            //MinecraftForge.EVENT_BUS.register(confHandler);
           // MinecraftForge.EVENT_BUS.register(new KeyInputEvent());
            //MinecraftForge.EVENT_BUS.register(new TooltipEvent());
        } else {
            System.err.println("ForgeEssentials client does nothing on servers. You should remove it!");
        }
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

}
