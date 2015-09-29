package com.forgeessentials.client;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.client.core.CommonProxy;
import com.forgeessentials.commons.BuildInfo;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ForgeEssentialsClient.MODID, name = "ForgeEssentials Client Addon", version = BuildInfo.BASE_VERSION, guiFactory = "com.forgeessentials.client.gui.forge.FEGUIFactory", useMetadata = true, dependencies = "required-after:Forge@[10.13.4.1448,)")
public class ForgeEssentialsClient
{
    
    public static final String MODID = "ForgeEssentialsClient";

    public static final Logger feclientlog = LogManager.getLogger("forgeessentials");

    @SidedProxy(clientSide = "com.forgeessentials.client.core.ClientProxy", serverSide = "com.forgeessentials.client.core.CommonProxy")
    protected static CommonProxy proxy;

    @Instance("ForgeEssentialsClient")
    protected static ForgeEssentialsClient instance;

    protected static boolean serverHasFE;

    /* ------------------------------------------------------------ */

    @NetworkCheckHandler
    public boolean getServerMods(Map<String, String> map, Side side)
    {
        if (side.equals(Side.SERVER))
        {
            if (map.containsKey("ForgeEssentials"))
            {
                serverHasFE = true;
                feclientlog.info("The server is running ForgeEssentials.");
            }
        }
        return true;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        if (e.getSide() == Side.SERVER)
            feclientlog.error("ForgeEssentials client does nothing on servers. You should remove it!");
        proxy.doPreInit(e);
    }

    @EventHandler
    public void load(FMLInitializationEvent e)
    {
        proxy.load(e);
    }

    /* ------------------------------------------------------------ */

    public static boolean serverHasFE()
    {
        return serverHasFE;
    }

}
