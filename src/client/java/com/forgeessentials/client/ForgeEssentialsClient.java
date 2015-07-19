package com.forgeessentials.client;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.util.DummyProxy;
import com.forgeessentials.commons.BuildInfo;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "ForgeEssentialsClient", name = "Forge Essentials Client Addon", version = BuildInfo.VERSION, guiFactory = "com.forgeessentials.client.gui.forge.FEGUIFactory", useMetadata = true)
public class ForgeEssentialsClient {

    public static Logger feclientlog;

    @SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.util.DummyProxy")
    public static DummyProxy proxy;

    public boolean serverHasFE;

    public static boolean allowCUI;
    public static boolean allowQRCodeRender;

    @Instance("ForgeEssentialsClient")
    public static ForgeEssentialsClient instance;

    @NetworkCheckHandler
    public boolean getServerMods(Map<String, String> map, Side side)
    {
        if (!side.equals(Side.SERVER))
        {
            return true;
        }
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
