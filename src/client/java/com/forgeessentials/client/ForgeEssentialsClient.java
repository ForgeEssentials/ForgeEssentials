package com.forgeessentials.client;

import com.forgeessentials.client.core.PlayerInfoClient;
import com.forgeessentials.client.util.DummyProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod(modid = "ForgeEssentialsClient", name = "Forge Essentials Client Addon", version = "%VERSION%", guiFactory = "com.forgeessentials.client.gui.forge.FEGUIFactory", useMetadata = true)
public class ForgeEssentialsClient {

    public static Logger feclientlog;

    @SideOnly(Side.CLIENT)
    public static PlayerInfoClient info;

    @SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.util.DummyProxy")
    public static DummyProxy proxy;

    public boolean serverHasFE;

    public static boolean allowCUI;

    public static SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel("forgeessentials");

    @Instance("ForgeEssentialsClient")
    public static ForgeEssentialsClient instance;

    @NetworkCheckHandler
    public boolean getServerMods(Map<String, String> map, Side side)
    {
        if (map.containsKey("ForgeEssentials"))
        {
            serverHasFE = true;
            feclientlog.info("The server is running ForgeEssentials.");
        }

        return true;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        feclientlog = LogManager.getLogger("forgeessentials");

        if (FMLCommonHandler.instance().getSide().isServer())
        {
            feclientlog.error("ForgeEssentialsClient should not be installed on a server! It will be automatically disabled.");
        }
        proxy.doPreInit(e);
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        proxy.load(e);
    }

}
