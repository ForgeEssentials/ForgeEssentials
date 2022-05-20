package com.forgeessentials.client;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.FMLHandshakeMessages.S2CModList;
import net.minecraftforge.fml.network.NetworkEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.client.init.RegisterCommandEvent;

@Mod(ForgeEssentialsClient.MODID)
@Mod.EventBusSubscriber(modid = ForgeEssentialsClient.MODID, bus = Bus.MOD,value = Dist.CLIENT)
public class ForgeEssentialsClient
{
    
    public static final String MODID = "forgeessentialsclient";
    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    //@SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.core.CommonProxy")
    protected static ClientProxy proxy;

    protected static boolean serverHasFE;
    
    public ForgeEssentialsClient(){
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	ModLoadingContext.get().registerConfig(Type.COMMON, TutorialConfig.SPEC, "forgeessentialsclient.toml");
    	bus.addListener(this::commonsetup);
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


    /* ------------------------------------------------------------ */

    public static boolean serverHasFE()
    {
        return serverHasFE;
    }

}
