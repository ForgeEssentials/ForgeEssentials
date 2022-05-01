package com.forgeessentials.client;

import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.FMLHandshakeMessages.S2CModList;
import net.minecraftforge.fml.network.NetworkEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.proxy.IProxy;
import com.forgeessentials.commons.BuildInfo;

@Mod(ForgeEssentialsClient.MODID)
@Mod.EventBusSubscriber(modid = ForgeEssentialsClient.MODID, bus = Bus.MOD)
public class ForgeEssentialsClient
{
    
    public static final String MODID = "forgeessentialsclient";

    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    public static IProxy proxy = DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientScreenManager);
    //@SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.core.CommonProxy")
    //protected static CommonProxy proxy;

    public static ForgeEssentialsClient instance;

    protected static boolean serverHasFE;
    
    public ForgeEssentialsClient(){
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::init);
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

    public void init(FMLCommonSetupEvent event) {
        if (FMLEnvironment.dist.isClient()) {
        	
            //changedWindowTitle=null;
            //confHandler=ConfigurationHandler.getInstance();
            //confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
            //KeyHandler.init();
            //System.out.println("on Init, confHandler is "+confHandler);
            MinecraftForge.EVENT_BUS.register(this);
            
            //MinecraftForge.EVENT_BUS.register(confHandler);
           // MinecraftForge.EVENT_BUS.register(new KeyInputEvent());
            //MinecraftForge.EVENT_BUS.register(new TooltipEvent());
        } else {
            System.err.println("ForgeEssentials client does nothing on servers. You should remove it!");
        }
    }


    /* ------------------------------------------------------------ */

    public static boolean serverHasFE()
    {
        return serverHasFE;
    }

}
